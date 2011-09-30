package com.jayway.forest.core;

/**
 */
public class Response {

    private String message;
    private Integer status;

    public Response( Integer status, String message ) {
        this.status = status;
        this.message = message;
    }

    public Integer status() {
        return status;
    }

    public String message() {
        return message;
    }
}
