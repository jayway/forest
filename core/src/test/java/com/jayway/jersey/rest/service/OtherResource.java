package com.jayway.jersey.rest.service;

import com.jayway.jersey.rest.dto.StringDTO;
import com.jayway.jersey.rest.roles.DeletableResource;
import com.jayway.jersey.rest.roles.IdResource;
import com.jayway.jersey.rest.roles.DescribedResource;
import com.jayway.jersey.rest.resource.Resource;

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
