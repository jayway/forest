package com.jayway.jersey.rest.service;

import com.jayway.jersey.rest.constraint.RequiresRoles;
import com.jayway.jersey.rest.resource.DeletableResource;
import com.jayway.jersey.rest.resource.IdResource;
import com.jayway.jersey.rest.resource.IndexResource;
import com.jayway.jersey.rest.resource.Resource;

/**
 */
public class ConstraintResource extends Resource implements IdResource, IndexResource, DeletableResource {

    @Override
    @RequiresRoles( String.class )
    public Resource id(String id) {
        return new RootResource();
    }


    @Override
    @RequiresRoles( String.class )
    public String index() {
        return role( String.class );
    }

    @Override
    @RequiresRoles( String.class )
    public void delete() {
        StateHolder.set("Delete invoked");
    }
}
