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

    public void command( StringDTO input ) {
        StateHolder.set(input);
    }

    public IntegerDTO addten( IntegerDTO number ) {
        number.setInteger( number.getInteger() + 10 );
        return number;
    }

    public StringDTO echo( StringDTO input ) {
        return input;
    }
    
    public OtherResource other() {
        return new OtherResource();
    }

    public void wrong2( StringDTO one, StringDTO two ) {
        
    }

    @RequiresRoles( String.class )
    public String constraint() {
        return role( String.class );
    }
}
