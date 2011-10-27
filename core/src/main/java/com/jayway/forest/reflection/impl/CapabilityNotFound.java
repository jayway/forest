package com.jayway.forest.reflection.impl;

import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.jayway.forest.core.MediaTypeHandler;
import com.jayway.forest.exceptions.MethodNotAllowedException;
import com.jayway.forest.exceptions.NotFoundException;
import com.jayway.forest.reflection.Capability;
import com.jayway.forest.reflection.RestReflection;
import com.jayway.forest.roles.Resource;
import org.omg.CosNaming.NamingContextPackage.NotFound;

public class CapabilityNotFound extends Capability {
	public static final CapabilityNotFound INSTANCE = new CapabilityNotFound();
	private CapabilityNotFound() {
		super(null, null, null);
	}
	@Override
	public Object get(HttpServletRequest request) {
		throw new NotFoundException();
	}
    @Override
    public void put(Map<String, String[]> formParams, InputStream stream, MediaTypeHandler mediaTypeHandler) {
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
		throw new NotFoundException();
	}
	@Override
	public String httpMethod() {
		return "N/A";
	}
	@Override
	public Object renderForm(RestReflection restReflection) {
		throw new UnsupportedOperationException();
	}
}
