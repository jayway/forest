package com.jayway.forest.legacy.roles;

/**
 * Implement this on a resource when
 * it is updatable.
 *
 * Http.put directly on the resource
 * will be routed to this method
 *
 */
public interface UpdatableResource<T> extends Resource {

    void update(T argument);

}
