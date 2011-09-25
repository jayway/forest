package com.jayway.jersey.rest.service;

import com.jayway.jersey.rest.constraint.RequiresRoles;
import com.jayway.jersey.rest.roles.DeletableResource;
import com.jayway.jersey.rest.roles.IdResource;
import com.jayway.jersey.rest.roles.DescribedResource;
import com.jayway.jersey.rest.resource.Resource;

/**
 */
public class ConstraintResource extends Resource implements IdResource, DescribedResource, DeletableResource {

    @Override
    @RequiresRoles( String.class )
    public Resource id(String id) {
        return new RootResource();
    }


    @Override
    @RequiresRoles( String.class )
    public String description() {
        return context(String.class);
    }

    @Override
    @RequiresRoles( String.class )
    public void delete() {
        StateHolder.set("Delete invoked");
    }
}
