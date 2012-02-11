package com.jayway.forest.legacy.mediatype;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.jayway.forest.legacy.servlet.Response;

public class SimpleErrorMessageBodyWriter extends AbstractMessageBodyWriter<Response>{
	
	private final Charset charset;

	public SimpleErrorMessageBodyWriter(MediaType mediaType, Charset charset) {
		super(Response.class, mediaType);
		this.charset = charset;
	}

	@Override
	public void writeTo(Response response, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream out) throws IOException, WebApplicationException {
        OutputStreamWriter writer = new OutputStreamWriter( out, charset);
        writer.write(response.message());
        writer.flush();
	}

}
