package com.jayway.forest.legacy.service;

import com.jayway.forest.legacy.dto.StringAndIntegerDTO;
import com.jayway.forest.legacy.dto.StringDTO;
import com.jayway.forest.legacy.roles.*;

/**
 */
public class OtherResource implements Resource, IdResource, ReadableResource<StringDTO>, DeletableResource, CreatableResource<StringAndIntegerDTO> {

    @Override
    public Resource id(String id) {
        return new RootResource();
    }

    public ConstraintResource constraint() {
        return new ConstraintResource();
    }

    @Override
    public StringDTO read() {
        StateHolder.set( new StringDTO((String) StateHolder.get()) );
        return (StringDTO) StateHolder.get();
    }

    @Override
    public void delete() {
        StateHolder.set("Delete invoked");
    }

    @Override
    public Linkable create( StringAndIntegerDTO argument ) {
        return new Linkable( argument.getString() + "/" + argument.getInteger().toString(), argument.getString() );
    }

}
