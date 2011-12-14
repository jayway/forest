package com.jayway.forest.service;

import com.jayway.forest.constraint.RolesInContext;
import com.jayway.forest.core.RoleManager;
import com.jayway.forest.roles.*;

/**
 */
public class ConstraintResource implements Resource, IdResource, ReadableResource<String>, DeletableResource {

    @Override
    @RolesInContext( String.class )
    public Resource id(String id) {
        return new RootResource();
    }


    @Override
    @RolesInContext( String.class )
    public String read() {
        return RoleManager.role(String.class);
    }

    @Override
    @RolesInContext( String.class )
    public void delete() {
        StateHolder.set("Delete invoked");
    }
}
