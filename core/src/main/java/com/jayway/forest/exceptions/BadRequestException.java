package com.jayway.forest.exceptions;

/**
 */
public class BadRequestException extends RuntimeException {
	private static final long serialVersionUID = 1;
    private String message;

    public BadRequestException() {
    }

    public BadRequestException( String message ) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
}
