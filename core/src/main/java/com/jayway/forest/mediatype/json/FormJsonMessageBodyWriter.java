package com.jayway.forest.mediatype.json;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.jayway.forest.reflection.FormCapability;

public class FormJsonMessageBodyWriter extends JsonMessageBodyWriter<FormCapability> {

	public FormJsonMessageBodyWriter(Charset charset) {
		super(FormCapability.class, charset);
	}

	@Override
	public void writeTo(FormCapability baseReflection, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream out) throws IOException, WebApplicationException {
        OutputStreamWriter writer = new OutputStreamWriter( out, charset);
        appendMethod(writer, baseReflection );
        writer.flush();
	}
}
