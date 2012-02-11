package com.jayway.forest.roles;

import javax.ws.rs.POST;

/**
 * Implement this on a resource when
 * it is updatable.
 *
 * Http.put directly on the resource
 * will be routed to this method
 *
 */
public interface UpdatableResource<T> extends Resource {

	@POST
    void update(T argument);

}
