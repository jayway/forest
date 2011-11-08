package com.jayway.forest.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.forest.core.MediaTypeHandler;
import com.jayway.forest.di.DependencyInjectionSPI;
import com.jayway.forest.exceptions.AbstractHtmlException;
import com.jayway.forest.exceptions.CreatedException;
import com.jayway.forest.exceptions.NotFoundException;
import com.jayway.forest.exceptions.RenderTemplateException;
import com.jayway.forest.exceptions.WrappedException;
import com.jayway.forest.reflection.Capabilities;
import com.jayway.forest.reflection.Capability;
import com.jayway.forest.reflection.RestReflection;
import com.jayway.forest.reflection.impl.BaseReflection;
import com.jayway.forest.reflection.impl.PagedSortedListResponse;
import com.jayway.forest.roles.Linkable;
import com.jayway.forest.roles.UriInfo;

/**
 */
public class ResponseHandler {
    private static Logger log = LoggerFactory.getLogger(ResponseHandler.class);

    public static final String SUCCESS_RESPONSE = "Operation completed successfully";

    private MediaTypeHandlerContainer mediaTypeHandlerContainer;
    private MediaTypeHandler mediaTypeHandler;
    private HttpServletResponse response;
    private ExceptionMapper exceptionMapper;

    public ResponseHandler( HttpServletRequest request, HttpServletResponse response, MediaTypeHandlerContainer mediaTypeHandlerContainer, ExceptionMapper exceptionMapper, DependencyInjectionSPI dependencyInjectionSPI ) throws IOException {
		mediaTypeHandler = new MediaTypeHandler(request, response );
        this.response = response;
        this.mediaTypeHandlerContainer = mediaTypeHandlerContainer;
        this.exceptionMapper = exceptionMapper;

        if ( request.getPathInfo() == null ) {
             response.sendError(404);
             throw new NotFoundException();
         }
         
        // add request specifics to the current context
        dependencyInjectionSPI.clear();
        dependencyInjectionSPI.addRequestContext(UriInfo.class, new UriInfo(request));
        dependencyInjectionSPI.addRequestContext(HttpServletRequest.class, request);
        dependencyInjectionSPI.addRequestContext(HttpServletResponse.class, response);
    }

    public void handleResponse( Object responseObject ) {
        if (responseObject != null) {
            try {
            	OutputStream out = response.getOutputStream();
                if ( responseObject instanceof Capabilities ) {
                    restReflection().renderCapabilities(out, (Capabilities) responseObject );
                } else if ( responseObject instanceof PagedSortedListResponse) {
                    restReflection().renderListResponse(out, (PagedSortedListResponse) responseObject);
                } else if ( responseObject instanceof Response ) {
                    Response error = (Response) responseObject;
                    response.setStatus( error.status() );
                    restReflection().renderError(out, error);
                } else if ( responseObject instanceof CreatedException ) {
                    response.setStatus( ((CreatedException) responseObject).getCode() );
                    response.addHeader( "Location", ((CreatedException) responseObject).getLinkable().getHref() );
                    restReflection().renderCreatedResponse( out, ((CreatedException) responseObject).getLinkable() );
                } else {
                    restReflection().renderQueryResponse( out, responseObject );
                }
				out.flush();
            } catch (IOException e) {
                response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
                log.error("Error sending response", e);
            }
        }
    }

    private RestReflection restReflection() {
		return mediaTypeHandlerContainer.restReflection(mediaTypeHandler.accept());
	}

	public void invoke( RestfulServlet.Runner runner ) {
        Object responseObject;
        try {
            responseObject = runner.run(mediaTypeHandler);
        } catch ( Exception e ) {
            if ( e instanceof CreatedException ) {
                responseObject = e;
            } else {
                responseObject = mapInternalException(e);
            }
        }
        handleResponse(responseObject);
    }

    private Response mapInternalException( Exception e ) {
        if ( e instanceof WrappedException ) {
            Throwable cause = e.getCause();
            if ( cause instanceof Exception ) {
                e = (Exception) cause;
            }
        }

        if ( exceptionMapper != null ) {
            Response response = exceptionMapper.map(e);
            if (response != null) {
                return response;
            }
        }

        if ( e instanceof RenderTemplateException ) {
            RenderTemplateException render = (RenderTemplateException) e;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Capability capability = render.getCapability();
            if (capability instanceof BaseReflection) {
            	try {
					restReflection().renderForm(out, (BaseReflection)capability);
				} catch (IOException e1) {
					throw new RuntimeException(e1);
				}
            }
            return new Response( render.getCode(), new String(out.toByteArray()) );
        } else if ( e instanceof AbstractHtmlException ) {
            return new Response( ((AbstractHtmlException) e).getCode(), e.getMessage() );
        } else {
            log.error(e.getClass().getSimpleName() + " is not mapped", e);
            if ( e.getMessage() == null ) {
                return new Response( HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getClass().getSimpleName() );
            } else {
                return new Response( HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage() );
            }
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
