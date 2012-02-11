package com.jayway.forest.legacy.reflection.impl;

import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.jayway.forest.legacy.core.MediaTypeHandler;
import com.jayway.forest.legacy.exceptions.NotFoundException;
import com.jayway.forest.legacy.reflection.Capability;
import com.jayway.forest.legacy.roles.IdResource;
import com.jayway.forest.legacy.roles.Resource;

public class CapabilityIdResource extends Capability {

	private final IdResource idResource;

	public CapabilityIdResource(IdResource idResource, String name, String documentation) {
		super(name, documentation, idResource.getClass().getSimpleName()+"Id");
		this.idResource = idResource;
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
        return idResource.id( path );
	}

	@Override
	public String httpMethod() {
		return "GET";
	}
}
