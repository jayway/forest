package com.jayway.forest.legacy.mediatype.html;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.jayway.forest.legacy.roles.Linkable;

public class LinkableHtmlMessageBodyWriter extends HtmlMessageBodyWriter<Linkable>{
	
	public LinkableHtmlMessageBodyWriter(Charset charset, String cssUrl) {
		super(Linkable.class, charset, cssUrl);
	}

	@Override
	public void writeTo(Linkable linkable, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream out) throws IOException, WebApplicationException {
        OutputStreamWriter writer = new OutputStreamWriter( out, charset);
        writeHeader(writer);
        writer.write("<code>Location:</code> <a href='" + linkable.getUri() + "' rel='"+linkable.getRel()+"'>"+linkable.getName() +"</a>");
        writeFooter(writer);
        writer.flush();
	}

}
