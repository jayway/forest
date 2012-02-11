package com.jayway.forest.legacy.service;

import com.jayway.forest.legacy.constraint.RolesInContext;
import com.jayway.forest.legacy.core.RoleManager;
import com.jayway.forest.legacy.roles.*;

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
