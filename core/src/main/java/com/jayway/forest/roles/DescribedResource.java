package com.jayway.forest.roles;


/**
 * Let the resource class implement this method 
 * to automatically output the 'description' result in the
 * capabilities of the resource
 */
public interface DescribedResource extends Resource {
    Object description();
}
