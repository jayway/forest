package com.jayway.forest.writers.html;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import com.jayway.forest.hypermedia.HyperMediaResponse;

@Provider
public class HtmlHyperMediaResponseWriter<T> implements MessageBodyWriter<HyperMediaResponse<T>> {
    private Charset charset = Charset.forName("UTF-8");
	private Template template;
	
	@Context
	private Providers providers;
    
    public HtmlHyperMediaResponseWriter() {
    	try {
			this.template = new VelocityEngineHolder().get().getTemplate("com/jayway/forest/hypermedia.html.vm", "utf-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return type.equals(HyperMediaResponse.class) && mediaType.isCompatible(MediaType.TEXT_HTML_TYPE);
	}

	@Override
	public long getSize(HyperMediaResponse<T> t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	@Override
	public void writeTo(HyperMediaResponse<T> response, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream out) throws IOException, WebApplicationException {

		String body = null;
		if (response.getBody() != null) {
			MessageBodyWriter<T> messageBodyWriter = providers.getMessageBodyWriter(response.getBodyClass(), response.getBodyClass(), annotations, mediaType);
			ByteArrayOutputStream bodyStream = new ByteArrayOutputStream();
			messageBodyWriter.writeTo(response.getBody(), response.getClass(), response.getClass(), annotations, mediaType, httpHeaders, bodyStream);
			body = new String(bodyStream.toByteArray(), charset);
		}
        try {
            VelocityContext context = new VelocityContext();
            context.put( "response", response);
            context.put( "body", body);
            OutputStreamWriter writer = new OutputStreamWriter( out, charset);
            template.merge( context, writer );
            writer.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	}
}
