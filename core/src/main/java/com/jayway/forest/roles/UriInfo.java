package com.jayway.forest.roles;

/**
 */
public class UriInfo {
    private String baseUrl;
    private String relativeUrl;

    public UriInfo( String baseUrl ) {
        this.baseUrl = baseUrl;
        this.relativeUrl = "";
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getRelativeUrl() {
        return relativeUrl;
    }

    public String getSelf() {
        return baseUrl + relativeUrl;
    }

    public void addPath( String path ) {
        relativeUrl += path + '/';
    }
}
