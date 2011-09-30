package com.jayway.forest.roles;


/**
 * Base class for resources that has named sub resources.
 * E.g. suppose there is a PersonsResource which has a
 * PersonResource as sub resource. The URL pattern would be
 * /persons/12345/. For the PersonResource with id <i>12345</i>
 * to be identified as a sub resource the PersonsResource has
 * to extend NamedResource.
 *
 */
public interface IdResource extends Resource {

    Resource id( String id);

}
