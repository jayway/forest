package com.jayway.forest.writers.json;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import com.jayway.forest.writers.AbstractHyperMediaResponseWriter;

@Provider
public class JsonHyperMediaResponseWriter<T> extends AbstractHyperMediaResponseWriter<T> {
    public JsonHyperMediaResponseWriter() {
    	super(MediaType.APPLICATION_JSON_TYPE, "com/jayway/forest/hypermedia.json.vm");
	}
}
