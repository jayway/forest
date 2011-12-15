package com.jayway.forest.roles;

import javax.ws.rs.GET;


/**
 * Let the resource class implement this method 
 * to automatically output the 'read' result in the
 * capabilities of the resource
 */
public interface ReadableResource<T> extends Resource {
	@GET
    T read();
}
