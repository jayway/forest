package com.jayway.jersey.rest.service;

import com.jayway.jersey.rest.dto.StringDTO;
import com.jayway.jersey.rest.resource.DeletableResource;
import com.jayway.jersey.rest.resource.IdResource;
import com.jayway.jersey.rest.resource.IndexResource;
import com.jayway.jersey.rest.resource.Resource;

/**
 */
public class OtherResource extends Resource implements IdResource, IndexResource, DeletableResource {

    @Override
    public Resource id(String id) {
        return new RootResource();
    }

    public ConstraintResource constraint() {
        return new ConstraintResource();
    }

    @Override
    public StringDTO index() {
        StateHolder.set( new StringDTO((String) StateHolder.get()) );
        return (StringDTO) StateHolder.get();
    }

    @Override
    public void delete() {
        StateHolder.set("Delete invoked");
    }
}
