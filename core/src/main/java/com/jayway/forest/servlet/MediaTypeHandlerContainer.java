package com.jayway.forest.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyWriter;

import com.jayway.forest.exceptions.UnsupportedMediaTypeException;

public class MediaTypeHandlerContainer {
	private final List<MessageBodyWriter> handlers = new LinkedList<MessageBodyWriter>();
	
	public <T> void addHandler(MessageBodyWriter<T> writer) {
		handlers.add(writer);
	}
	
	public <T> void write(OutputStream out, MediaType mediaType, Class<T> clazz, T responseObject) throws IOException {
		findMessageBodyWriter(mediaType, clazz).writeTo(responseObject, clazz, clazz, null, mediaType, null, out);
	}
	
	public <T> MessageBodyWriter<T> findMessageBodyWriter(MediaType mediaType, Class<T> clazz) {
		for (MessageBodyWriter handler : handlers) {
			if (handler.isWriteable(clazz, clazz, null, mediaType)) {
				return (MessageBodyWriter<T>) handler;
			}
		}
		throw new UnsupportedMediaTypeException("No handler available for " + mediaType + " and class: " + clazz.getName());
	}
}
