package com.jayway.forest.writers.html;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import com.jayway.forest.writers.AbstractHyperMediaResponseWriter;

@Provider
public class HtmlHyperMediaResponseWriter<T> extends AbstractHyperMediaResponseWriter<T> {
    public HtmlHyperMediaResponseWriter() {
    	super(MediaType.TEXT_HTML_TYPE, "com/jayway/forest/hypermedia.html.vm");
	}
}
