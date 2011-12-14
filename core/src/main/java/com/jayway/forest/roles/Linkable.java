package com.jayway.forest.roles;

import static com.jayway.forest.core.RoleManager.role;

/**
 */
public class Linkable {

    private String uri;
    private String name;
    private String rel;

    public Linkable( String id ) {
    	this(id, id);
    }

    public Linkable( String id, String name ) {
        if ( !id.startsWith("http://")) {
            this.uri = role( UriInfo.class).getSelf() + id;
        } else {
            this.uri = id;
        }
        if ( !id.endsWith("/") ) {
            this.uri = this.uri + "/";
        }
        this.name = name;
    }

    public Linkable( String id, String name, String rel ) {
        this(id, name);
        this.rel = rel;
    }


    public String getUri() {
        return uri;
    }

    public String getName() {
        return name;
    }

    public String getRel() {
        return rel;
    }

    public void setRel( String rel ) {
        this.rel = rel;
    }
}
