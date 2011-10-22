package com.jayway.forest.dto;

import com.jayway.forest.roles.Linkable;

/**
 */
public class MyLinkable extends Linkable {

    private String test;

    public MyLinkable(String href, String name, String rel, String description, String test) {
        super(href, name, rel, description);
        this.test = test;
    }
}
