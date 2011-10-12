package com.jayway.forest.service;

import com.jayway.forest.constraint.RolesInContext;
import com.jayway.forest.dto.IntegerDTO;
import com.jayway.forest.exceptions.NotFoundException;
import com.jayway.forest.roles.CreatableResource;
import com.jayway.forest.roles.Linkable;
import com.jayway.forest.roles.Resource;
import com.jayway.forest.roles.Template;

import java.util.List;

import static com.jayway.forest.core.RoleManager.*;

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

    @RolesInContext( String.class )
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

    private String content() {
        return "Template Content";
    }

    public void updatewithtemplate( @Template("content") String content ) {
    }

    public String throwingnotfound() {
        throw new NotFoundException("Bad stuff");
    }
}
