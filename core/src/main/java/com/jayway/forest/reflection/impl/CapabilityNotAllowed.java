package com.jayway.forest.reflection.impl;

import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jayway.forest.core.MediaTypeHandler;
import com.jayway.forest.exceptions.NotFoundException;
import com.jayway.forest.exceptions.UnauthorizedException;
import com.jayway.forest.reflection.Capability;
import com.jayway.forest.reflection.RestReflection;
import com.jayway.forest.roles.Resource;

public class CapabilityNotAllowed extends Capability {
	public CapabilityNotAllowed(String name) {
		super(name, null, null);
	}
	@Override
	public Object get(HttpServletRequest request) {
		throw new UnauthorizedException();
	}
	@Override
	public void post(Map<String, String[]> formParams, InputStream stream, MediaTypeHandler mediaTypeHandler, HttpServletResponse response ) {
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
		return "GET";
	}
	@Override
	public Object renderForm(RestReflection restReflection) {
		throw new UnsupportedOperationException();
	}
}
