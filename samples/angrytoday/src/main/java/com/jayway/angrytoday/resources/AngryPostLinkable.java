package com.jayway.angrytoday.resources;

import com.jayway.forest.roles.Linkable;

import java.util.Set;

public class AngryPostLinkable extends Linkable {

    private String message;
    private Set<String> tags;
    private String comment;

    public AngryPostLinkable(String id, String message, Set<String> tags, String comment ) {
        super(id, message);
        this.tags = tags;
        this.comment = comment;
    }
}
