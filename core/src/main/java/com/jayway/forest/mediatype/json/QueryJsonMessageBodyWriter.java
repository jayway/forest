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

import com.jayway.forest.core.JSONHelper;

public class QueryJsonMessageBodyWriter extends JsonMessageBodyWriter<Object>{
	
	public QueryJsonMessageBodyWriter(Charset charset) {
		super(Object.class, charset);
	}

	@Override
	public void writeTo(Object responseObject, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream out) throws IOException, WebApplicationException {
        OutputStreamWriter writer = new OutputStreamWriter( out, charset);
        if ( responseObject instanceof String ) {
        	writer.write("\"");
        	writer.write(responseObject.toString());
        	writer.write("\"");
        } else {
        	writer.write(new JSONHelper().toJSON(responseObject).toString());
        }
        writer.flush();
	}

}
