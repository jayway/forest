package com.jayway.forest.reflection;

import com.jayway.forest.reflection.impl.SortParameter;

import java.util.List;

/**
 * An instance of this interface is sent to
 * methods returning List
 * </br>
 * If it is not touched by the method the
 * framework will assume that sorting and paging
 * has not been done and it will handle sorting
 * and paging itself.
 */public interface PagingSortingParameter {
    Long getPage();

    Long getPageSize();

    void setPageSize(Long pageSize);

    void setTotalElements(Long totalElements);

    Long offset();

    List<SortParameter> sortParameters();

    void addSortByField(String field);
}
