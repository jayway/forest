package com.jayway.forest.reflection.impl;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.jayway.forest.core.MediaTypeHandler;
import com.jayway.forest.exceptions.BadRequestException;
import com.jayway.forest.exceptions.MethodNotAllowedRenderTemplateException;
import com.jayway.forest.exceptions.NotFoundException;
import com.jayway.forest.reflection.RestReflection;
import com.jayway.forest.roles.Resource;

public class QueryCapability extends BaseReflectionCapability {
    protected final Resource resource;
    protected final Method method;

    public QueryCapability(Resource resource, Method method, String documentation, String rel) {
        super(method.getName(), documentation, rel);
        this.resource = resource;
        this.method = method;
    }

    @Override
    public Object get(HttpServletRequest request) {
        @SuppressWarnings("unchecked")
        Map<String, String[]> queryParams = request.getParameterMap();
        if ( queryParams.size() == 0 && method.getParameterTypes().length > 0) {
            throw new MethodNotAllowedRenderTemplateException( this );
        } else {
            try {
                if ( method.getParameterTypes().length == 0 ) {
                    return method.invoke( resource );
                }
                Object[] args = new Object[method.getParameterTypes().length];
                try {
                    for ( int i=0; i<args.length; i++) {
                        Class<?> type = method.getParameterTypes()[i];
                        args[i] = mapArguments( type, queryParams, "argument"+(i+1));
                    }
                    return method.invoke( resource, args);
                } catch (BadRequestException e ) {
                    // some arguments was passed but some was missing
                    throw new MethodNotAllowedRenderTemplateException( this );
                }
            } catch ( IllegalAccessException e) {
                throw internalServerError( e );
            } catch ( InvocationTargetException e) {
                log.error("Exception occurred: ", e);
                throw internalServerError( e );
            }
        }
    }

    @Override
    public void post(Map<String, String[]> formParams, InputStream stream, MediaTypeHandler mediaTypeHandler) {
        throw new NotFoundException();
    }

    @Override
    public void delete() {
        throw new NotFoundException();
    }

    @Override
    public Resource subResource(String path) {
        throw new NotFoundException();
    }
    @Override
    public String httpMethod() {
        return "GET";
    }

    @Override
    public Object renderForm(RestReflection restReflection) {
        return restReflection.renderQueryForm(method);
    }
}
