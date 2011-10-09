package com.jayway.forest.reflection.impl;

import com.jayway.forest.reflection.PagingSortingParameter;

import java.util.*;


final public class PagingSortingParameterImpl extends Touchable implements PagingSortingParameter {

    private Integer page;
    private Integer pageSize;
    private Long totalElements;
    private List<SortParameter> parameters;
    private Set<String> addedSortingFields;

    protected PagingSortingParameterImpl(Integer page, Integer pageSize, String sortByFromUrl) {
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

    @Override
    public Integer getPage() {
        touch();
        return page;
    }

    @Override
    public Integer getPageSize() {
        touch();
        return pageSize;
    }

    @Override
    public void setPageSize(int pageSize) {
        touch();
        this.pageSize = pageSize;
    }

    @Override
    public void setTotalElements(Long totalElements) {
        touch();
        this.totalElements = totalElements;
    }

    @Override
    public Integer offset() {
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
