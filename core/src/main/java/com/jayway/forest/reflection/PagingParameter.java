package com.jayway.forest.reflection;

/**
 */
final public class PagingParameter extends Touchable {

    private Integer page;
    private Integer pageSize;
    private int totalElements;

    protected PagingParameter( Integer page, Integer pageSize) {
        if ( page == null ) {
            this.page = 1;
        } else {
            this.page = page;
        }
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


    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        touch();
        this.totalElements = totalElements;
    }

    public Integer offset() {
        touch();
        return (page-1)*pageSize;
    }

}
