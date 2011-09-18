package com.jayway.jersey.rest.service;

import com.jayway.jersey.rest.constraint.RequiresRoles;
import com.jayway.jersey.rest.dto.IntegerDTO;
import com.jayway.jersey.rest.dto.StringDTO;
import com.jayway.jersey.rest.resource.Resource;

/**
 */
public class RootResource extends Resource {

    public RootResource sub() {
        return new RootResource();
    }

    public void command( String input ) {
        StateHolder.set(input);
    }

    public IntegerDTO addten( Integer number ) {
        return new IntegerDTO( number + 10);
    }

    public IntegerDTO add( Integer first, IntegerDTO second ) {
        return new IntegerDTO( first + second.getInteger() );
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
        return role( String.class );
    }
}
