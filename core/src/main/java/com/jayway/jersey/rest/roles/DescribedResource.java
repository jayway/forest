package com.jayway.jersey.rest.roles;


/**
 * Let the resource class implement this method 
 * to automatically output the 'description' result in the
 * capabilities of the resource
 */
public interface DescribedResource {
    Object description();
}
