package com.jayway.forest.roles;

/**
 */
public class SortParameter {
    private boolean ascending;
    private String name;

    protected SortParameter( String rawName ) {
        if ( rawName.startsWith("-") ) {
            ascending = false;
            name = rawName.substring(1);
        } else {
            ascending = true;
            name = rawName;
        }
    }

    public boolean isAscending() {
        return ascending;
    }

    public String name() {
        return name;
    }

}
