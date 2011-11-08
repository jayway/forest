package com.jayway.forest.reflection.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import com.jayway.forest.exceptions.UnsupportedMediaTypeException;
import com.jayway.forest.reflection.Capabilities;
import com.jayway.forest.reflection.RestReflection;
import com.jayway.forest.roles.Linkable;
import com.jayway.forest.servlet.Response;

public class BasicRestReflection implements RestReflection {
	
	protected final Charset charset;

	public BasicRestReflection(Charset charset) {
		this.charset = charset;
	}
	
	protected String emptyOrString(Object object) {
		if (object == null) {
			return "";
		}
		return object.toString();
	}

	@Override
	public void renderCapabilities(OutputStream out, Capabilities capabilities) throws IOException {
		throw new UnsupportedMediaTypeException();
	}

	@Override
	public void renderForm(OutputStream out, BaseReflection capability) throws IOException {
		throw new UnsupportedMediaTypeException();
	}

	@Override
	public void renderListResponse(OutputStream out, PagedSortedListResponse<?> responseObject) throws IOException {
		throw new UnsupportedMediaTypeException();
	}

	@Override
	public void renderQueryResponse(OutputStream out, Object responseObject) throws IOException {
		throw new UnsupportedMediaTypeException();
	}

	@Override
	public void renderError(OutputStream out, Response response) throws IOException {
		throw new UnsupportedMediaTypeException();
	}

	@Override
	public void renderCreatedResponse(OutputStream out, Linkable linkable) throws IOException {
		throw new UnsupportedMediaTypeException();
	}
}
