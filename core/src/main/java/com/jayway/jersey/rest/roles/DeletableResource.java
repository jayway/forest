package com.jayway.jersey.rest.roles;

/**
 * Implement this on a resource to let it
 * be deletable i.e. Http Delete to that
 * method... should be the resource itself...
 *
 * if the capabilities html links to / and calls
 * it delete it will only re-discover...
 */
public interface DeletableResource {

    void delete();

}
