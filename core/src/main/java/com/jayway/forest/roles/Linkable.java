package com.jayway.forest.roles;

/**
 */
public class Linkable {

    private String id;
    private String name;
    private String rel;

    public Linkable( String id, String name, String rel ) {
        this(id, name);
        this.rel = rel;
    }

    public Linkable( String id, String name ) {
        this.id = id;
        this.name = name;
    }


    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String rel() {
        return rel;
    }

}
