package com.jayway.forest.roles;

/**
 */
public class Linkable {

    private String id;
    private String name;

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
}
