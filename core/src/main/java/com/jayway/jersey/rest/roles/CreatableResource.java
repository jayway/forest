package com.jayway.jersey.rest.roles;

/**
 * Implement this on a resource when
 * it can create something.
 *
 * Http.post directly on the resource
 * will be routed to this method
 *
 */
public interface CreatableResource {

    Linkable create( Object... arguments);

}
