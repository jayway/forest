package com.jayway.forest.dto;

import com.jayway.forest.roles.Linkable;

/**
 */
public class MyLinkable extends Linkable {

    private String test;
    private String description;

    public MyLinkable(String uri, String name, String description, String test) {
        super(uri, name);
        this.description = description;
        this.test = test;
    }
}
