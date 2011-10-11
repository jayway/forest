package com.jayway.forest.reflection.impl;

import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.jayway.forest.core.MediaTypeHandler;
import com.jayway.forest.exceptions.NotFoundException;
import com.jayway.forest.reflection.Capability;
import com.jayway.forest.reflection.RestReflection;
import com.jayway.forest.roles.IdResource;
import com.jayway.forest.roles.Resource;

public class IdCapability extends Capability {

	private final IdResource idResource;

	public IdCapability(IdResource idResource, String name, String documentation) {
		super(name, documentation, null);
		this.idResource = idResource;
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
        return idResource.id( path );
	}

	@Override
	public String httpMethod() {
		return "GET";
	}

	@Override
	public Object renderForm(RestReflection restReflection) {
		throw new UnsupportedOperationException();
	}
}
