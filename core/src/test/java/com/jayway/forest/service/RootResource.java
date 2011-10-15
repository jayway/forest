package com.jayway.forest.service;

import com.jayway.forest.constraint.RolesInContext;
import com.jayway.forest.dto.IntegerDTO;
import com.jayway.forest.dto.StringDTO;
import com.jayway.forest.exceptions.NotFoundException;
import com.jayway.forest.roles.CreatableResource;
import com.jayway.forest.roles.Linkable;
import com.jayway.forest.roles.Resource;
import com.jayway.forest.roles.Template;

import java.util.ArrayList;
import java.util.List;

import static com.jayway.forest.core.RoleManager.role;

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

    private Integer defaultInt() {
        return 17;
    }
    private IntegerDTO defaultIntDTO() {
        return new IntegerDTO(63);
    }
    private IntegerDTO evil() {
        throw new IllegalArgumentException();
    }

    public Integer addwithtemplates( @Template("defaultInt") Integer first, @Template("defaultIntDTO") IntegerDTO second ) {
        return first + second.getInteger();
    }

    public Integer addwithwrongtemplates( @Template("badwrongname") Integer first, @Template("evil") IntegerDTO second ) {
        return first + second.getInteger();
    }

    public void addcommand( Integer first, IntegerDTO second ) {
        StateHolder.set( first + second.getInteger() );
    }

    public String echo( @Template("content") String input ) {
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


    public String withwrongtemplatetype( @Template("wrongType") String name ) {
        return null;
    }
    private Double wrongType() {
        return 5.0;
    }

    public String withnonexistingtemplate( @Template("nonexistent") String name ) {
        return null;
    }

    private String withargument( String argument ) {
        return "cannot be invoked by framework";
    }
    public String templatemethodwithargument( @Template("withargument") String arg) {
        return null;
    }

    public List<String> list() {
        List<String> list = new ArrayList<String>();
        list.add("world");
        list.add( "hello");
        return list;
    }

    public List<StringDTO> liststringdto() {
        List<StringDTO> list = new ArrayList<StringDTO>();
        list.add( new StringDTO("world"));
        list.add( new StringDTO("hello"));
        return list;
    }

    public List<StringDTO> listhowlong( Integer size ) {
        List<StringDTO> list = new ArrayList<StringDTO>(size);
        for ( int i=0; i<size; i++ ) {
            list.add( new StringDTO(""+i) );
        }
        return list;
    }
}

