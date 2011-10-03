package com.jayway.forest.roles;

/**
 */
public class Linkable {

    private String href;
    private String name;
    private String rel;

    public Linkable( String href, String name, String rel ) {
        this(href, name);
        this.rel = rel;
    }

    public Linkable( String href, String name ) {
        this.href = href;
        this.name = name;
    }


    public String href() {
        return href;
    }

    public String name() {
        return name;
    }

    public String rel() {
        return rel;
    }

}
