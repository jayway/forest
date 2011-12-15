package com.jayway.forest.roles;

import javax.ws.rs.PUT;

/**
 * Implement this on a resource when
 * it can create something.
 *
 * Http.post directly on the resource
 * will be routed to this method
 *
 */
public interface CreatableResource<T> extends Resource {

	@PUT
    Linkable create( T argument);

}
