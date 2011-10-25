package com.jayway.forest.reflection.impl;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jayway.forest.core.MediaTypeHandler;
import com.jayway.forest.exceptions.NotFoundException;
import com.jayway.forest.reflection.Capability;
import com.jayway.forest.reflection.RestReflection;
import com.jayway.forest.roles.Resource;

public class SubResource extends Capability {
	private final Resource resource;
	private final Method method;
	public SubResource(Resource resource, Method method, String documentation) {
		super(method.getName(), documentation, null);
		this.resource = resource;
		this.method = method;
	}
	@Override
	public Object get(HttpServletRequest request) {
		throw new NotFoundException();
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
        try {
            return (Resource) method.invoke( resource );
        } catch ( IllegalAccessException e) {
            e.printStackTrace();
            throw internalServerError( e );
        } catch ( InvocationTargetException e) {
            if ( e.getCause() instanceof RuntimeException ) {
                throw (RuntimeException) e.getCause();
            }
            log.error("Error invoking resource method", e);
            throw internalServerError( e );
        }
	}
	@Override
	public String httpMethod() {
		return "GET";
	}

	@Override
	public Object renderForm(RestReflection restReflection) {
		throw new UnsupportedOperationException();
	}

    @Override
    public String href() {
        return super.href() + "/";
    }
}
