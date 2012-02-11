package com.jayway.forest.legacy.roles;


/**
 * @Deprecated
 * To adhere to the CRUD naming convention
 * the functionality of DescribedResource is
 * moved to ReadableResource
 *
 */
@Deprecated
public interface DescribedResource extends Resource {
    Object description();
}
