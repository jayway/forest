package com.jayway.forest.exceptions;

/**
 */
abstract public class AbstractHtmlException extends RuntimeException {

    private int code;
    private String message;

    public AbstractHtmlException( int code, String message ) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
