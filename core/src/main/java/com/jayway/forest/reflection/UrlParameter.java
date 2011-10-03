package com.jayway.forest.reflection;

import java.util.Map;

/**
 * finds the search/sort parameters from the request
 */
public class UrlParameter {
    private String sortBy;
    private Integer page;
    private Integer pageSize;

    public UrlParameter( Map<String, String[]> queryParams ) {
        String page = getFirst(queryParams, "page");
        String pageSize = getFirst(queryParams, "pageSize");

        sortBy = getFirst(queryParams, "sortBy");
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


    private String getFirst( Map<String, String[]> map, String key ) {
        String[] strings = map.get(key);
        if ( strings == null ) return null;
        return strings[0];
    }

    public String linkTo( Integer page ) {
        StringBuilder sb = new StringBuilder("page=").append( page );
        appendParameters( sb );
        return sb.toString();
    }

    private void appendParameters( StringBuilder sb ) {
        if ( pageSize != null ) {
            sb.append("&pageSize=").append( pageSize );
        }
        if ( sortBy != null ) {
            sb.append("&sortBy=").append( sortBy );
        }
    }


    public Integer page() {
        return page;
    }

    public Integer pageSize() {
        return pageSize;
    }

    public String sortBy() {
        return sortBy;
    }
}
