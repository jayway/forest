package com.jayway.forest.roles;

import static com.jayway.forest.core.RoleManager.role;

import com.jayway.forest.hypermedia.Link;

// TODO: remove this class

/**
 */
public class Linkable extends Link {

    private String uri;
    private String name;
    private String rel;

    public Linkable( String id ) {
    	this(id, id);
    }

    public Linkable( String href, String name, String rel ) {
        this(href, name);
        this.rel = rel;
    }

    public Linkable( String uri, String id ) {
    	super(uri, "httpMethod", id, "documentation");
        if ( !uri.startsWith("http://")) {
            this.uri = role( UriInfo.class).getSelf() + uri;
        } else {
            this.uri = id;
        }
        if ( !id.endsWith("/") ) {
            this.uri = this.uri + "/";
        }
        this.name = id;
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
