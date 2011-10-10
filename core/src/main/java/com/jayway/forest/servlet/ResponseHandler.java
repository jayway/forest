package com.jayway.forest.servlet;

import com.jayway.forest.core.MediaTypeHandler;
import com.jayway.forest.di.DependencyInjectionSPI;
import com.jayway.forest.exceptions.AbstractHtmlException;
import com.jayway.forest.exceptions.MethodNotAllowedRenderTemplateException;
import com.jayway.forest.reflection.Capabilities;
import com.jayway.forest.reflection.Capability;
import com.jayway.forest.reflection.RestReflection;
import com.jayway.forest.reflection.impl.AtomRestReflection;
import com.jayway.forest.reflection.impl.HtmlRestReflection;
import com.jayway.forest.reflection.impl.JsonRestReflection;
import com.jayway.forest.reflection.impl.PagedSortedListResponse;
import com.jayway.forest.roles.BaseUrl;
import com.jayway.forest.roles.Linkable;
import com.jayway.forest.roles.UriInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class ResponseHandler {

    private static Map<String, RestReflection> reflectors;

    static {
        reflectors = new HashMap<String, RestReflection>();
        reflectors.put(MediaTypeHandler.APPLICATION_JSON, JsonRestReflection.INSTANCE);
        reflectors.put(MediaTypeHandler.TEXT_HTML, HtmlRestReflection.INSTANCE);
        reflectors.put(MediaTypeHandler.APPLICATION_ATOM, AtomRestReflection.INSTANCE);
    }

    private RestReflection restReflection() {
        return reflectors.get( mediaTypeHandler.accept() );
    }

    public static final String SUCCESS_RESPONSE = "Operation completed successfully";

    private MediaTypeHandler mediaTypeHandler;
    private HttpServletResponse response;
    private ExceptionMapper exceptionMapper;

    public ResponseHandler( HttpServletRequest request, HttpServletResponse response, ExceptionMapper exceptionMapper, DependencyInjectionSPI dependencyInjectionSPI ) {
        mediaTypeHandler = new MediaTypeHandler(request, response );
        this.response = response;
        this.exceptionMapper = exceptionMapper;

        // add request specifics to the current context
        String url = request.getRequestURL().toString();
        String path = request.getPathInfo();
        dependencyInjectionSPI.clear();

        String base = url.substring(0, url.length() - path.length() + 1);
        dependencyInjectionSPI.addRequestContext( UriInfo.class, new UriInfo( base ) );
        dependencyInjectionSPI.addRequestContext(BaseUrl.class, new BaseUrl(base));
        dependencyInjectionSPI.addRequestContext(HttpServletRequest.class, request);
        dependencyInjectionSPI.addRequestContext(HttpServletResponse.class, response);
    }

    public void handleResponse( Object responseObject ) throws IOException {
        if (responseObject != null) {
            OutputStreamWriter writer = new OutputStreamWriter( response.getOutputStream(), Charset.forName("UTF-8"));
            String responseString;
            if ( responseObject instanceof Capabilities ) {
                responseString = restReflection().renderCapabilities((Capabilities) responseObject ).toString();
            } else if ( responseObject instanceof PagedSortedListResponse) {
                responseString = restReflection().renderListResponse( (PagedSortedListResponse) responseObject ).toString();
            } else {
                responseString = restReflection().renderQueryResponse( responseObject ).toString();
            }
            writer.write( responseString, 0, responseString.length() );
            writer.flush();
        }
    }

    public void handleError( Response responseError ) {
        try {
            response.setStatus( responseError.status() );
            response.getOutputStream().print(  responseError.message() );
        } catch ( IOException ioe) {
            response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
        }

    }

    public void invoke( HttpServletRequest req, HttpServletResponse resp, RestfulServlet.Runner runner ) {
        try {
            handleResponse(runner.run(req, resp, mediaTypeHandler));
        } catch ( Exception e ) {
            handleError(mapInternalException(e));
        }
    }

    private Response mapInternalException( Exception e ) {
        if ( exceptionMapper != null ) {
            Response response = exceptionMapper.map(e);
            if (response != null) {
                return response;
            }
        }

        if ( e instanceof MethodNotAllowedRenderTemplateException ) {
            Capability method = ((MethodNotAllowedRenderTemplateException) e).method();
            Object form = method.renderForm(restReflection());
            return new Response(((MethodNotAllowedRenderTemplateException) e).getCode(), form.toString() );
        } else if ( e instanceof AbstractHtmlException ) {
            return new Response( ((AbstractHtmlException) e).getCode(), e.getMessage() );
        } else {
            return new Response( HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage() );
        }
    }

    public static <T> List<Linkable> transform( Iterable<T> from, Transform<T> transformer ) {
        List<Linkable> links = new ArrayList<Linkable>();
        for ( T t : from ) {
            links.add( transformer.transform( t) );
        }
        return links;
    }


    public interface Transform<T> {
        Linkable transform( T t );
    }
}
