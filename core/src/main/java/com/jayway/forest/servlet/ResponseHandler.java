package com.jayway.forest.servlet;

import com.jayway.forest.core.JSONHelper;
import com.jayway.forest.core.MediaTypeHandler;
import com.jayway.forest.exceptions.*;
import com.jayway.forest.reflection.Capabilities;
import com.jayway.forest.reflection.HtmlRestReflection;
import com.jayway.forest.reflection.JsonRestReflection;
import com.jayway.forest.reflection.Capability;
import com.jayway.forest.reflection.RestReflection;
import com.jayway.forest.roles.Linkable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
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

    public ResponseHandler( HttpServletRequest request, HttpServletResponse response, ExceptionMapper exceptionMapper ) {
        mediaTypeHandler = new MediaTypeHandler(request.getHeader("Accept"), request.getHeader("Content-Type"));
        this.response = response;
        this.exceptionMapper = exceptionMapper;
    }

    public void handleResponse( Object responseObject ) throws IOException {
        if (responseObject != null) {
            if ( responseObject instanceof Capabilities ) {
                responseObject = restReflection().renderCapabilities((Capabilities) responseObject ).toString();
                response.getOutputStream().print( responseObject.toString() );
            } else {
                if (mediaTypeHandler.acceptJSSON()) {
                    if (responseObject instanceof String) {
                        response.getOutputStream().print("\"" + responseObject + "\"");
                    } else {
                        response.getOutputStream().print(new JSONHelper().toJSON(responseObject).toString());
                    }
                } else {
                    response.getOutputStream().print( responseObject.toString() );
                }
            }
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
            return new Response( SC_BAD_REQUEST, "Bad Request");
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
