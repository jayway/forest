package com.jayway.forest.roles;

import javax.ws.rs.PUT;

/**
 * Implement this on a resource when
 * it can create a sub resource.
 *
 * Http.post directly on the resource
 * will be routed to this method
 *
 */
public interface CreatableResource<T> extends IdResource {

	@PUT
    Linkable create( T argument);

}
