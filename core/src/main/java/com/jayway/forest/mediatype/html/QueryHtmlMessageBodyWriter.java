package com.jayway.forest.mediatype.html;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.jayway.forest.core.JSONHelper;

public class QueryHtmlMessageBodyWriter extends HtmlMessageBodyWriter<Object>{
	
	public QueryHtmlMessageBodyWriter(Charset charset, String cssUrl) {
		super(Object.class, charset, cssUrl);
	}

	@Override
	public void writeTo(Object responseObject, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream out) throws IOException, WebApplicationException {
        OutputStreamWriter writer = new OutputStreamWriter( out, charset);
        if ( responseObject instanceof String ) {
        	writer.write(responseObject.toString());
        } else {
        	writer.write(new JSONHelper().toJSON(responseObject).toString());
        }
        writer.flush();
	}

}
