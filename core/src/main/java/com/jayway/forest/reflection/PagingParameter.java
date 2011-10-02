package com.jayway.forest.reflection;

/**
 */
final public class PagingParameter extends Touchable {

    private Integer page;
    private Integer pageSize;

    protected PagingParameter( Integer page, Integer pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }

    public Integer getPage() {
        touch();
        return page;
    }

    public Integer getPageSize() {
        touch();
        return pageSize;
    }

    private int totalElements;
    private boolean hasNext;
    private boolean hasPrevious;
    private int preferredPageSize;

    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
        touch();
    }

    public boolean hasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        touch();
        this.hasNext = hasNext;
    }

    public boolean hasPrevious() {
        return hasPrevious;
    }

    public void setHasPrevious(boolean hasPrevious) {
        touch();
        this.hasPrevious = hasPrevious;
    }

    public int getPreferredPageSize() {
        return preferredPageSize;
    }

    public void setPreferredPageSize(int preferredPageSize) {
        touch();
        this.preferredPageSize = preferredPageSize;
    }
}
