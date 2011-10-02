package com.jayway.forest.reflection;

/**
 */
final public class SortingParameter extends Touchable {

    public static final String SORT_ORDER_DESCENDING = "desc";
    public static final String SORT_ORDER_ASCENDING = "asc";
    private String sortBy;
    private String sortOrder;
    private Boolean setInURL;

    protected SortingParameter( String sortBy, String sortOrder, Boolean setInURL ) {
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
        this.setInURL = setInURL;
    }

    public String getSortBy() {
        return sortBy;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public Boolean setInURL() {
        return setInURL;
    }
}
