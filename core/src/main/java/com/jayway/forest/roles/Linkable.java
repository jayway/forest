package com.jayway.forest.roles;

import static com.jayway.forest.core.RoleManager.role;

/**
 */
public class Linkable {

    private String href;
    private String name;
    private String rel;
    private String description;

    public Linkable( String href, String name, String rel, String description ) {
        this( href, name, rel);
        this.description = description;
    }

    public Linkable( String href, String name, String rel ) {
        this(href, name);
        this.rel = rel;
    }

    public Linkable( String href, String name ) {
        if ( !href.startsWith("http://")) {
            this.href = role( UriInfo.class).getSelf() + href;
        } else {
            this.href = href;
        }
        this.name = name;
    }

    public Linkable( String name ) {
    	this(name + "/", name);
    }

    public String getHref() {
        return href;
    }

    public String getName() {
        return name;
    }

    public String getRel() {
        return rel;
    }

    public String getDescription() {
        return description;
    }
}
