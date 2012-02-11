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

import com.jayway.forest.legacy.exceptions.AbstractHtmlException;
import com.jayway.forest.legacy.servlet.Response;

public class ErrorHtmlMessageBodyWriter extends HtmlMessageBodyWriter<Response>{
	
	public ErrorHtmlMessageBodyWriter(Charset charset, String cssUrl) {
		super(Response.class, charset, cssUrl);
	}

	@Override
	public void writeTo(Response response, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream out) throws IOException, WebApplicationException {
        OutputStreamWriter writer = new OutputStreamWriter( out, charset);
        if ( response.status() == 405) {
            writer.write(response.message());
            writer.flush();
            return;
        }
        writeHeader(writer);
        writer.append("<h1>HTTP Error ").append(response.status().toString()).append("</h1>");
        String description = AbstractHtmlException.messageMapping.get(response.status());
        if ( description != null ) {
            writer.append("<code>").append( description).append("</code>");
        }
        writer.append("<h2>Message</h2>").append( response.message() );
        writeFooter(writer);
        writer.flush();
	}

}
