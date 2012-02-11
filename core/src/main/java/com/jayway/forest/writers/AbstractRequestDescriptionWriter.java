package com.jayway.forest.writers;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.velocity.VelocityContext;

import com.jayway.forest.hypermedia.RequestDescription;

public class AbstractRequestDescriptionWriter extends VelocityWriter<RequestDescription> {
    
    public AbstractRequestDescriptionWriter(MediaType mediaType, String template) {
    	super(RequestDescription.class, mediaType, template);
	}

	@Override
	public final void writeTo(RequestDescription description, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream out) throws IOException, WebApplicationException {
        VelocityContext context = new VelocityContext();
        context.put( "description", description);
        super.write(out, context);
	}
}
