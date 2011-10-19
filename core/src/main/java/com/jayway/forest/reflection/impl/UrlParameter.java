package com.jayway.forest.reflection.impl;

import java.util.HashMap;
import java.util.Map;

/**
 * Holder for the actual url parameters of the request
 * <br></br>
 * This is only used by the framework
 *
 */
public final class UrlParameter {
    private String sortBy;
    private Integer page;
    private Integer pageSize;
    private Map<String, String> parameters;

    protected UrlParameter( Map<String, String[]> queryParams ) {
        parameters = new HashMap<String, String>();

        for (Map.Entry<String, String[]> entry : queryParams.entrySet()) {
            // multiple parameters with same name not supported
            parameters.put(entry.getKey(), entry.getValue()[0] );
        }
        String pageSize = parameters.get( "pageSize" );
        String page = parameters.get( "page" );
        sortBy = parameters.get( "sortBy" );

        this.page = parse( page );
        this.pageSize = parse( pageSize );
    }

    private Integer parse( String number ) {
        if ( number == null ) return null;
        try {
            return Integer.parseInt( number );
        } catch (NumberFormatException e ) {
            return null;
        }
    }

    protected String linkSortBy( String sort, boolean ascending ) {
        StringBuilder sb = new StringBuilder("?sortBy=");
        if ( !ascending ) sb.append("-");
        sb.append(sort);
        String sortBy = parameters.remove("sortBy");
        appendParameters(sb);
        if ( sortBy != null ) {
            parameters.put( "sortBy", sortBy );
        }
        return sb.toString();
    }

    protected String linkTo( Integer page ) {
        StringBuilder sb = new StringBuilder("?page=").append( page );
        String currentPage = parameters.remove( "page" );
        appendParameters( sb );
        if ( currentPage != null ) {
            parameters.put( "page", currentPage );
        }
        return sb.toString();
    }

    private void appendParameters( StringBuilder sb ) {
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            sb.append("&").append( entry.getKey() ).append("=").append(entry.getValue());
        }
    }

    protected void setPageSize( Integer pageSize ) {
        this.pageSize = pageSize;
        parameters.remove( "pageSize" );
        parameters.put( "pageSize", ""+pageSize);
    }

    protected Integer page() {
        return page;
    }

    protected Integer pageSize() {
        return pageSize;
    }

    protected String sortBy() {
        return sortBy;
    }
}
