package com.jayway.forest.legacy.reflection.impl;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.jayway.forest.legacy.core.JSONHelper;
import com.jayway.forest.legacy.core.MediaTypeHandler;
import com.jayway.forest.legacy.exceptions.MethodNotAllowedRenderTemplateException;
import com.jayway.forest.legacy.exceptions.NotFoundException;
import com.jayway.forest.legacy.exceptions.UnsupportedMediaTypeException;
import com.jayway.forest.legacy.exceptions.WrappedException;
import com.jayway.forest.legacy.reflection.FormCapability;
import com.jayway.forest.legacy.roles.Resource;

public class CapabilityCommand extends FormCapability {

	public CapabilityCommand(Method method, Resource resource, String documentation, String rel) {
		super(method, resource, method.getName(), documentation, rel);
	}

	@Override
	public Object get(HttpServletRequest request) {
		throw new MethodNotAllowedRenderTemplateException( this );
	}

    @Override
    public void put(Map<String, String[]> formParams, InputStream stream, MediaTypeHandler mediaTypeHandler) {
        throw new MethodNotAllowedRenderTemplateException( this );
    }

    @Override
	public void post(Map<String, String[]> formParams, InputStream stream, MediaTypeHandler mediaTypeHandler ) {
        Object[] arguments = stream == null ? arguments(method, formParams ) : arguments( method, stream, mediaTypeHandler );
        invokeCommand( arguments );

        /*if ( mediaTypeHandler.acceptHtml() ) {
            put(formParams, stream, mediaTypeHandler);
        } else {
            throw new MethodNotAllowedRenderTemplateException( this );
        }*/
	}

    @Override
    public void delete() {
        throw new MethodNotAllowedRenderTemplateException( this );
    }

    protected <T extends Resource> void invokeCommand(Object... arguments) {
        try {
            method.invoke( resource, arguments );
        } catch (InvocationTargetException e) {
            if ( e.getCause() instanceof RuntimeException ) {
                throw (RuntimeException) e.getCause();
            }
            throw new WrappedException( e.getCause() );
        } catch (IllegalAccessException e) {
            throw internalServerError( e );
        }
    }
	
    private Object[] arguments( Method m, InputStream stream, MediaTypeHandler mediaTypeHandler ) {
        if ( mediaTypeHandler.contentTypeJSON() ) {
            return new JSONHelper().handleArguments( m, stream );
        }
        // TODO support other types
        throw new UnsupportedMediaTypeException();
    }

	@Override
	public Resource subResource(String path) {
		throw new NotFoundException();
	}

	@Override
	public String httpMethod() {
		return "POST";
	}
}
