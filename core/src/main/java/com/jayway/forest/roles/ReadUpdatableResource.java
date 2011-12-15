package com.jayway.forest.roles;

/**
 * This interface does automatic templating of the read method as
 * template input to the update method,
 *
 * A convenience interface
 *
 */
public interface ReadUpdatableResource<T> extends ReadableResource<T>, UpdatableResource<T> {

}
