package com.jayway.forest.legacy.dto;

import com.jayway.forest.legacy.roles.Linkable;

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
