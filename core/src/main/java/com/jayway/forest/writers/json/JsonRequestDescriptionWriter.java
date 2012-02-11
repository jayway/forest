package com.jayway.forest.writers.json;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import com.jayway.forest.writers.AbstractRequestDescriptionWriter;

@Provider
public class JsonRequestDescriptionWriter extends AbstractRequestDescriptionWriter {
    
    public JsonRequestDescriptionWriter() {
    	super(MediaType.APPLICATION_JSON_TYPE, "com/jayway/forest/form.json.vm");
	}
}
