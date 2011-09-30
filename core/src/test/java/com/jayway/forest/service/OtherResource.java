package com.jayway.forest.service;

import com.jayway.forest.dto.StringDTO;
import com.jayway.forest.roles.DeletableResource;
import com.jayway.forest.roles.DescribedResource;
import com.jayway.forest.roles.IdResource;
import com.jayway.forest.roles.Resource;

/**
 */
public class OtherResource implements Resource, IdResource, DescribedResource, DeletableResource {

    @Override
    public Resource id(String id) {
        return new RootResource();
    }

    public ConstraintResource constraint() {
        return new ConstraintResource();
    }

    @Override
    public StringDTO description() {
        StateHolder.set( new StringDTO((String) StateHolder.get()) );
        return (StringDTO) StateHolder.get();
    }

    @Override
    public void delete() {
        StateHolder.set("Delete invoked");
    }
}
