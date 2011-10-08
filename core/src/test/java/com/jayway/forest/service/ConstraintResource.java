package com.jayway.forest.service;

import com.jayway.forest.constraint.RolesInContext;
import com.jayway.forest.core.RoleManager;
import com.jayway.forest.roles.DeletableResource;
import com.jayway.forest.roles.DescribedResource;
import com.jayway.forest.roles.IdResource;
import com.jayway.forest.roles.Resource;

/**
 */
public class ConstraintResource implements Resource, IdResource, DescribedResource, DeletableResource {

    @Override
    @RolesInContext( String.class )
    public Resource id(String id) {
        return new RootResource();
    }


    @Override
    @RolesInContext( String.class )
    public String description() {
        return RoleManager.role(String.class);
    }

    @Override
    @RolesInContext( String.class )
    public void delete() {
        StateHolder.set("Delete invoked");
    }
}
