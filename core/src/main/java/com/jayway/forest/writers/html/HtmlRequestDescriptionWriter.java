package com.jayway.forest.writers.html;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.apache.velocity.VelocityContext;

import com.jayway.forest.hypermedia.RequestDescription;

@Provider
public class HtmlRequestDescriptionWriter extends VelocityWriter<RequestDescription> {
    
    public HtmlRequestDescriptionWriter() {
    	super(RequestDescription.class, MediaType.TEXT_HTML_TYPE, "com/jayway/forest/form.html.vm");
	}

	@Override
	public void writeTo(RequestDescription description, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream out) throws IOException, WebApplicationException {
        VelocityContext context = new VelocityContext();
        context.put( "description", description);
        super.write(out, context);
	}
}
