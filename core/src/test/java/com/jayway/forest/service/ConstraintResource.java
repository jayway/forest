package com.jayway.forest.service;

import com.jayway.forest.constraint.grove.RequiresRoles;
import com.jayway.forest.grove.RoleManager;
import com.jayway.forest.roles.DeletableResource;
import com.jayway.forest.roles.DescribedResource;
import com.jayway.forest.roles.IdResource;
import com.jayway.forest.roles.Resource;

/**
 */
public class ConstraintResource implements Resource, IdResource, DescribedResource, DeletableResource {

    @Override
    @RequiresRoles( String.class )
    public Resource id(String id) {
        return new RootResource();
    }


    @Override
    @RequiresRoles( String.class )
    public String description() {
        return RoleManager.role(String.class);
    }

    @Override
    @RequiresRoles( String.class )
    public void delete() {
        StateHolder.set("Delete invoked");
    }
}
