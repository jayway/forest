package com.jayway.jersey.rest.service;

import static com.jayway.forest.grove.RoleManager.role;

import com.jayway.forest.grove.RoleManager;
import com.jayway.jersey.rest.constraint.grove.RequiresRoles;
import com.jayway.jersey.rest.dto.IntegerDTO;
import com.jayway.jersey.rest.roles.CreatableResource;
import com.jayway.jersey.rest.resource.Resource;
import com.jayway.jersey.rest.roles.Linkable;

import java.util.List;

/**
 */
public class RootResource implements Resource, CreatableResource {

    public RootResource sub() {
        return new RootResource();
    }

    public void command( String input ) {
        StateHolder.set(input);
    }

    public void commandlist( List<String> list ) {
        StateHolder.set( "Success"+list.get(0) );
    }

    public void addtolist( List<String> list, String append ) {
        list.add( append );
        StateHolder.set( list );
    }

    public IntegerDTO addten( Integer number ) {
        return new IntegerDTO( number + 10);
    }

    public Integer add( Integer first, IntegerDTO second ) {
        return first + second.getInteger();
    }

    public void addcommand( Integer first, IntegerDTO second ) {
        StateHolder.set( first + second.getInteger() );
    }

    public String echo( String input ) {
        return input;
    }
    
    public OtherResource other() {
        return new OtherResource();
    }

    @RequiresRoles( String.class )
    public String constraint() {
        return role(String.class);
    }

    public void complex( List<List<List<String>>> list ) {
        list.get(0).get(0).add("NEW");
        StateHolder.set(list);
    }

    @Override
    public Linkable create(Object... arguments) {
        return new Linkable( "1234", "jayway");
    }
}
