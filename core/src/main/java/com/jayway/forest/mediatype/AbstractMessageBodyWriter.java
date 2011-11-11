package com.jayway.forest.mediatype;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyWriter;

public abstract class AbstractMessageBodyWriter<T> implements MessageBodyWriter<T> {
	
	private final Class<T> clazz;
	private final MediaType mediaType;
	
	public AbstractMessageBodyWriter(Class<T> clazz, MediaType mediaType) {
		this.clazz = clazz;
		this.mediaType = mediaType;
	}
	
	@Override
	public long getSize(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return -1;
	}
	
	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return this.mediaType.isCompatible(mediaType) && this.clazz.isAssignableFrom(type);
	}
	
	protected String emptyOrString(Object object) {
		if (object == null) {
			return "";
		}
		return object.toString();
	}
}
