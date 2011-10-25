package com.jayway.forest.reflection.impl;

import com.jayway.forest.core.JSONHelper;
import com.jayway.forest.core.MediaTypeHandler;
import com.jayway.forest.exceptions.*;
import com.jayway.forest.reflection.RestReflection;
import com.jayway.forest.roles.DeletableResource;
import com.jayway.forest.roles.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class CommandCapability extends BaseReflectionCapability {
	protected final Method method;
	protected final Resource resource;

	public CommandCapability(Method method, Resource resource, String documentation, String rel) {
		super(method.getName(), documentation, rel);
		this.method = method;
		this.resource = resource;
	}

	@Override
	public Object get(HttpServletRequest request) {
		throw new MethodNotAllowedRenderTemplateException( this );
	}

	@Override
	public void post(Map<String, String[]> formParams, InputStream stream, MediaTypeHandler mediaTypeHandler ) {
        Object[] arguments = stream == null ? arguments(method, formParams ) : arguments( method, stream, mediaTypeHandler );
        invokeCommand( arguments );
	}

	public <T extends Resource> void invokeCommand(Object... arguments) {
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

    private Object[] arguments( Method m, Map<String, String[]> formParams ) {
        if ( m.getParameterTypes().length == 0 ) {
            return new Object[0];
        }
        Object[] args = new Object[m.getParameterTypes().length];

        for ( int i=0; i<args.length; i++ ) {
            Class<?> type = m.getParameterTypes()[i];
            args[i] = mapArguments( type, formParams, "argument"+(i+1) );
        }
        return args;
    }

	@Override
	public void delete() {
        ((DeletableResource) resource).delete();
	}

	@Override
	public Resource subResource(String path) {
		throw new NotFoundException();
	}

	@Override
	public String httpMethod() {
		return "PUT";
	}

	@Override
	public Object renderForm(RestReflection restReflection) {
		return restReflection.renderCommandForm(method, resource);
	}
}
