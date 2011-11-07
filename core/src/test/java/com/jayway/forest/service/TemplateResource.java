package com.jayway.forest.service;

import com.jayway.forest.dto.IntegerDTO;
import com.jayway.forest.roles.Resource;
import com.jayway.forest.roles.Template;

/**
 */
public class TemplateResource implements Resource {

    public Integer addwithtemplates( @Template("defaultInt") Integer first, @Template("defaultIntDTO") IntegerDTO second ) {
        return first + second.getInteger();
    }
    public Integer addwithwrongtemplates( @Template("badwrongname") Integer first, @Template("evil") IntegerDTO second ) {
        return first + second.getInteger();
    }
    public Integer witheviltemplates( @Template("evil") IntegerDTO i ) {
        return i.getInteger();
    }
    public String echo( @Template("content") String input ) {
        return input;
    }
    public void updatewithtemplate( @Template("content") String content ) {
    }
    public String withwrongtemplatetype( @Template("wrongType") String name ) {
        return null;
    }
    public String withpublictemplate( @Template("publicTemplate") String name ) {
        return null;
    }
    public String withnonexistingtemplate( @Template("nonexistent") String name ) {
        return null;
    }
    public String templatemethodwithargument( @Template("withargument") String arg) {
        return null;
    }
    public void command( String input ) {
    }
    public Integer add( Integer first, IntegerDTO second ) {
        return first + second.getInteger();
    }

    public String publicTemplate() {
        return "PUBLIC";
    }

    private Double wrongType() {
        return 5.0;
    }
    private String content() {
        return "Template Content";
    }
    private String withargument( String argument ) {
        return "cannot be invoked by framework";
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

}
