package com.jayway.forest.legacy.reflection.impl;

import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.jayway.forest.legacy.core.MediaTypeHandler;
import com.jayway.forest.legacy.exceptions.UnauthorizedException;
import com.jayway.forest.legacy.reflection.Capability;
import com.jayway.forest.legacy.roles.Resource;

public class CapabilityConstraintFailed extends Capability {
	public CapabilityConstraintFailed(String name) {
		super(name, null, null);
	}
	@Override
	public Object get(HttpServletRequest request) {
		throw new UnauthorizedException();
	}

    @Override
    public void put(Map<String, String[]> formParams, InputStream stream, MediaTypeHandler mediaTypeHandler) {
        throw new UnauthorizedException();
    }

    @Override
	public void post(Map<String, String[]> formParams, InputStream stream, MediaTypeHandler mediaTypeHandler ) {
		throw new UnauthorizedException();
	}
	@Override
	public void delete() {
		throw new UnauthorizedException();
	}
	@Override
	public Resource subResource(String path) {
		throw new UnauthorizedException();
	}
	@Override
	public String httpMethod() {
		throw new UnsupportedOperationException();
//		return "N/A";
	}
}
