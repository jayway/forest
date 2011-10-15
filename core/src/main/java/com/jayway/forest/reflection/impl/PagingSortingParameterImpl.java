package com.jayway.forest.reflection.impl;

import com.jayway.forest.reflection.PagingSortingParameter;

import java.util.*;


final public class PagingSortingParameterImpl extends Touchable implements PagingSortingParameter {

    private Long page;
    private Long pageSize;
    private Long totalElements;
    private List<SortParameter> parameters;
    private Set<String> addedSortingFields;

    protected PagingSortingParameterImpl(Long page, Long pageSize, String sortByFromUrl) {
        if ( page == null ) {
            this.page = 1l;
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

    @Override
    public Long getPage() {
        touch();
        return page;
    }

    @Override
    public Long getPageSize() {
        touch();
        return pageSize;
    }

    @Override
    public void setPageSize(Long pageSize) {
        touch();
        this.pageSize = pageSize;
    }

    @Override
    public void setTotalElements(Long totalElements) {
        touch();
        this.totalElements = totalElements;
    }

    @Override
    public Long offset() {
        touch();
        return (page-1)*pageSize;
    }

    @Override
    public List<SortParameter> sortParameters() {
        touch();
        return parameters;
    }

    @Override
    public void addSortByField(String field) {
        touch();
        if ( addedSortingFields == null ) addedSortingFields = new HashSet<String>();
        addedSortingFields.add( field );
        parameters.add( new SortParameter( field ));
    }

    protected Set<String> getAddedSortFields() {
        if ( addedSortingFields == null) return Collections.emptySet();
        return addedSortingFields;
    }
}
