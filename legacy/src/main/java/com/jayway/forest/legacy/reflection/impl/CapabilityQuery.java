package com.jayway.forest.legacy.reflection.impl;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.jayway.forest.legacy.core.MediaTypeHandler;
import com.jayway.forest.legacy.exceptions.BadRequestException;
import com.jayway.forest.legacy.exceptions.BadRequestRenderTemplateException;
import com.jayway.forest.legacy.exceptions.MethodNotAllowedRenderTemplateException;
import com.jayway.forest.legacy.exceptions.NotFoundException;
import com.jayway.forest.legacy.exceptions.WrappedException;
import com.jayway.forest.legacy.reflection.FormCapability;
import com.jayway.forest.legacy.roles.Resource;

public class CapabilityQuery extends FormCapability {

    public CapabilityQuery(Resource resource, Method method, String documentation, String rel) {
        super(method, resource, method.getName(), documentation, rel);
    }

    @Override
    public Object get(HttpServletRequest request) {
        @SuppressWarnings("unchecked")
        Map<String, String[]> queryParams = request.getParameterMap();
        if ( queryParams.size() == 0 && method.getParameterTypes().length > 0) {
            throw new BadRequestRenderTemplateException( this );
        } else {
            try {
                if ( method.getParameterTypes().length == 0 ) {
                    return method.invoke( resource );
                }
                Object[] args = arguments(method, queryParams);
                try {
                    return method.invoke( resource, args);
                } catch (BadRequestException e ) {
                    // some arguments was passed but some was missing
                    throw new BadRequestRenderTemplateException( this );
                }
            } catch ( IllegalAccessException e) {
                throw internalServerError( e );
            } catch ( InvocationTargetException e) {
                if ( e.getCause() instanceof RuntimeException ) {
                    throw (RuntimeException) e.getCause();
                }
                throw new WrappedException( e.getCause() );
            }
        }
    }

    @Override
    public void put(Map<String, String[]> formParams, InputStream stream, MediaTypeHandler mediaTypeHandler) {
        throw new MethodNotAllowedRenderTemplateException( this );
    }

    @Override
    public void post(Map<String, String[]> formParams, InputStream stream, MediaTypeHandler mediaTypeHandler ) {
        throw new MethodNotAllowedRenderTemplateException( this );
    }

    @Override
    public void delete() {
        throw new MethodNotAllowedRenderTemplateException( this );
    }

    @Override
    public Resource subResource(String path) {
        throw new NotFoundException();
    }
    @Override
    public String httpMethod() {
        return "GET";
    }
}
