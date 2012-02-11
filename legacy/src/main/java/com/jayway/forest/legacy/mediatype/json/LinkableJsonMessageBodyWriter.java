package com.jayway.forest.legacy.mediatype.json;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.jayway.forest.legacy.reflection.impl.CapabilityLinkable;
import com.jayway.forest.legacy.roles.Linkable;

public class LinkableJsonMessageBodyWriter extends JsonMessageBodyWriter<Linkable>{
	
	public LinkableJsonMessageBodyWriter(Charset charset) {
		super(Linkable.class, charset);
	}

	@Override
	public void writeTo(Linkable linkable, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream out) throws IOException, WebApplicationException {
        OutputStreamWriter writer = new OutputStreamWriter( out, charset);
        appendMethod(writer, new CapabilityLinkable( linkable ) );
        writer.flush();
	}

}
