package com.jayway.forest.roles;

/**
 * Implement this on a resource when
 * it can create something.
 *
 * Http.post directly on the resource
 * will be routed to this method
 *
 */
public interface CreatableResource extends Resource {

    Linkable create( Object... arguments);

}
