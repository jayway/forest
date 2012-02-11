package com.jayway.forest.writers;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

public abstract class VelocityWriter<T> implements MessageBodyWriter<T> {
    protected static final Charset charset = Charset.forName("UTF-8");
	private Template template;
	
	private final Class<?> expectedType;
	private final MediaType mediaType;
    
    public VelocityWriter(Class<?> expectedType, MediaType mediaType, String templateName) {
    	this.expectedType = expectedType;
		this.mediaType = mediaType;
		try {
			this.template = new VelocityEngineHolder().get().getTemplate(templateName, "utf-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return type.equals(expectedType) && mediaType.isCompatible(this.mediaType);
	}

	@Override
	public long getSize(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	protected void write(OutputStream out, VelocityContext context) {
        try {
            OutputStreamWriter writer = new OutputStreamWriter( out, charset);
            template.merge( context, writer );
            writer.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	}

}
