package com.jayway.forest.service;

import com.jayway.forest.dto.StringDTO;
import com.jayway.forest.roles.*;

/**
 */
public class OtherResource implements Resource, IdResource, DescribedResource, DeletableResource, CreatableResource {

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

    @Override
    public Linkable create( Object... arguments ) {
        StringBuilder sb = new StringBuilder();
        for (Object argument : arguments) {
            sb.append( argument );
        }
        StateHolder.set( sb.toString() );
        return new Linkable( "1234", "jayway", "appendabletest" );
    }
}
