package com.jayway.forest.reflection;

import java.util.*;

/**
 * An instance of this class is sent to
 * methods returning List
 * </br>
 * If it is not touched by the method the
 * framework will assume that sorting and paging
 * has not been done and it will handle sorting
 * and paging itself.
 */
final public class PagingSortingParameter extends Touchable {

    private Integer page;
    private Integer pageSize;
    private Long totalElements;
    private List<SortParameter> parameters;
    private Set<String> addedSortingFields;

    protected PagingSortingParameter(Integer page, Integer pageSize, String sortByFromUrl ) {
        if ( page == null ) {
            this.page = 1;
        } else {
            this.page = page;
        }
        this.pageSize = pageSize;
        parameters = new LinkedList<SortParameter>();
        if ( sortByFromUrl == null || sortByFromUrl.length() == 0) return;

        String[] sortBy = sortByFromUrl.split(",");
        for ( String rawParameter : sortBy ) {
            parameters.add( new SortParameter( rawParameter ));
        }
    }

    protected Long getTotalElements() {
        return totalElements;
    }

    public Integer getPage() {
        touch();
        return page;
    }

    public Integer getPageSize() {
        touch();
        return pageSize;
    }

    public void setPageSize( int pageSize ) {
        touch();
        this.pageSize = pageSize;
    }

    public void setTotalElements(Long totalElements) {
        touch();
        this.totalElements = totalElements;
    }

    public Integer offset() {
        touch();
        return (page-1)*pageSize;
    }

    public Iterator<SortParameter> sortParameters() {
        touch();
        return parameters.iterator();
    }

    public void addSortByField( String field ) {
        touch();
        if ( addedSortingFields == null ) addedSortingFields = new HashSet<String>();
        addedSortingFields.add( field );
    }

    protected Set<String> getAddedSortFields() {
        if ( addedSortingFields == null) return Collections.emptySet();
        return addedSortingFields;
    }
}
