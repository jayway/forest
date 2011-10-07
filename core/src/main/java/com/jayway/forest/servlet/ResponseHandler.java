package com.jayway.forest.servlet;

import com.jayway.forest.core.JSONHelper;
import com.jayway.forest.core.MediaTypeHandler;
import com.jayway.forest.di.DependencyInjectionSPI;
import com.jayway.forest.exceptions.*;
import com.jayway.forest.reflection.*;
import com.jayway.forest.roles.BaseUrl;
import com.jayway.forest.roles.Linkable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javax.servlet.http.HttpServletResponse.*;

/**
 */
public class ResponseHandler {

    private static Map<String, RestReflection> reflectors;

    static {
        reflectors = new HashMap<String, RestReflection>();
        reflectors.put(MediaTypeHandler.APPLICATION_JSON, JsonRestReflection.INSTANCE);
        reflectors.put(MediaTypeHandler.TEXT_HTML, HtmlRestReflection.INSTANCE);
    }

    private RestReflection restReflection() {
        if ( mediaTypeHandler.acceptJSSON() ) {
            return reflectors.get( MediaTypeHandler.APPLICATION_JSON );
        } else {
            return reflectors.get( MediaTypeHandler.TEXT_HTML );
        }
    }

    public static final String SUCCESS_RESPONSE = "Operation completed successfully";

    private MediaTypeHandler mediaTypeHandler;
    private HttpServletResponse response;
    private ExceptionMapper exceptionMapper;

    public ResponseHandler( HttpServletRequest request, HttpServletResponse response, ExceptionMapper exceptionMapper, DependencyInjectionSPI dependencyInjectionSPI ) {
        //mediaTypeHandler = new MediaTypeHandler(request.getHeader("Accept"), request.getHeader("Content-Type"));
        mediaTypeHandler = new MediaTypeHandler(request, response );
        this.response = response;
        this.exceptionMapper = exceptionMapper;

        // add request specifics to the current context
        // old version added request and response, but that seems too much
        String url = request.getRequestURL().toString();
        String path = request.getPathInfo();
        dependencyInjectionSPI.addRequestContext(BaseUrl.class, new BaseUrl(url.substring(0, url.length() - path.length() + 1 ) ));
        dependencyInjectionSPI.addRequestContext(HttpServletRequest.class, request);
        dependencyInjectionSPI.addRequestContext(HttpServletResponse.class, response);
    }

    public void handleResponse( Object responseObject ) throws IOException {
        if (responseObject != null) {
            OutputStreamWriter writer = new OutputStreamWriter( response.getOutputStream(), Charset.forName("UTF-8"));
            String responseString;
            if ( responseObject instanceof Capabilities ) {
                responseString = restReflection().renderCapabilities((Capabilities) responseObject ).toString();
            } else if ( responseObject instanceof PagedSortedListResponse ) {
                responseString = restReflection().renderListResponse( (PagedSortedListResponse) responseObject ).toString();
            } else {
                if (mediaTypeHandler.acceptJSSON()) {
                    if (responseObject instanceof String) {
                        responseString = "\"" + responseObject + "\"";
                    } else {
                        responseString = new JSONHelper().toJSON(responseObject).toString();
                    }
                } else {
                    if ( responseObject instanceof String ) {
                        responseString = responseObject.toString();
                    } else {
                        // complex object are JSON serialized for html output
                        responseString = new JSONHelper().toJSON(responseObject).toString();
                    }
                }
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
            //log
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

        if ( e instanceof BadRequestException) {
            String message = e.getMessage() != null ? e.getMessage() : "Bad Request";
            return new Response( SC_BAD_REQUEST, message );
        } else if ( e instanceof ConflictException) {
            return new Response( SC_CONFLICT,"Conflict" );
        } else if ( e instanceof InternalServerErrorException) {
            return new Response( SC_INTERNAL_SERVER_ERROR, "Internal Server Error" );
        } else if ( e instanceof MethodNotAllowedException) {
            return new Response(SC_METHOD_NOT_ALLOWED, "Method Not Allowed" );
        } else if ( e instanceof MethodNotAllowedRenderTemplateException) {
            Capability method = ((MethodNotAllowedRenderTemplateException) e).method();
            Object form = method.renderForm(restReflection());
            return new Response(SC_METHOD_NOT_ALLOWED, form.toString() );
        } else if ( e instanceof NotFoundException) {
            return new Response(SC_NOT_FOUND, "Not Found" );
        } else if ( e instanceof UnsupportedMediaTypeException ) {
            return new Response( SC_UNSUPPORTED_MEDIA_TYPE,  "Unsupported Media Type" );
        } else {
            return new Response( HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage() );
        }
    }

    public static Linkable map( Class<?> clazz, Object instance, String idFieldName, String nameFieldName ) {
        try {
            Field id = clazz.getDeclaredField(idFieldName);
            Field name = clazz.getDeclaredField(nameFieldName);
            id.setAccessible( true );
            name.setAccessible( true );
            return new Linkable( id.get( instance).toString(), name.get( instance ).toString() );
        } catch (NoSuchFieldException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    public static List<Linkable> mapList( Class<?> clazz, Iterable<?> instances, String idFieldName, String nameFieldName ) {
        List<Linkable> links = new ArrayList<Linkable>();
        for (Object instance : instances) {
            links.add( map( clazz, instance, idFieldName, nameFieldName) );
        }
        return links;
    }
}
