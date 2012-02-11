package com.jayway.forest.writers.html;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import com.jayway.forest.writers.AbstractRequestDescriptionWriter;

@Provider
public class HtmlRequestDescriptionWriter extends AbstractRequestDescriptionWriter {
    
    public HtmlRequestDescriptionWriter() {
    	super(MediaType.TEXT_HTML_TYPE, "com/jayway/forest/form.html.vm");
	}
}
