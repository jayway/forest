package com.jayway.forest.writers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Providers;

import org.apache.velocity.VelocityContext;

import com.jayway.forest.hypermedia.ResourceDescription;

public abstract class AbstractHyperMediaResponseWriter<T> extends VelocityWriter<ResourceDescription<T>> {
	@Context
	private Providers providers;
    
    public AbstractHyperMediaResponseWriter(MediaType mediaType, String template) {
    	super(ResourceDescription.class, mediaType, template);
	}

	@Override
	public void writeTo(ResourceDescription<T> response, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream out) throws IOException, WebApplicationException {
		String body = null;
		if (response.getBody() != null) {
			MessageBodyWriter<T> messageBodyWriter = providers.getMessageBodyWriter(response.getBodyClass(), response.getBodyClass(), annotations, mediaType);
			ByteArrayOutputStream bodyStream = new ByteArrayOutputStream();
			messageBodyWriter.writeTo(response.getBody(), response.getClass(), response.getClass(), annotations, mediaType, httpHeaders, bodyStream);
			body = new String(bodyStream.toByteArray(), charset);
		}
        VelocityContext context = new VelocityContext();
        context.put( "response", response);
        context.put( "body", body);
        super.write(out, context);
	}

}
