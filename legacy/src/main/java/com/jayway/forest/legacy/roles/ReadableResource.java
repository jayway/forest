package com.jayway.forest.legacy.roles;


/**
 * Let the resource class implement this method 
 * to automatically output the 'read' result in the
 * capabilities of the resource
 */
public interface ReadableResource<T> extends Resource {
    T read();
}
