package com.jayway.forest.reflection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class PagedSortedListResponse {
    private String name;
    private Integer page;
    private Integer pageSize;
    private Integer totalElements;
    private Integer totalPages;
    private List<?> list;
    private String next;
    private String previous;

    //private String self;
    private Map<String, String> orderByAsc;
    private Map<String, String> orderByDesc;


    public List<?> getList() {
        return list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Integer totalElements) {
        this.totalElements = totalElements;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public Map<String, String> getOrderByAsc() {
        return orderByAsc;
    }

    public void addOrderByAsc( String field, String href ) {
        if ( orderByAsc == null ) {
            orderByAsc = new HashMap<String, String>();
        }
        orderByAsc.put( field, href );
    }

    public Map<String, String> getOrderByDesc() {
        return orderByDesc;
    }

    public void addOrderByDesc( String field, String href ) {
        if ( orderByDesc == null ) {
            orderByDesc = new HashMap<String, String>();
        }
        orderByDesc.put( field, href );
    }
}
