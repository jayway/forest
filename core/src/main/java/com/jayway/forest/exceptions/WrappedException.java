package com.jayway.forest.exceptions;

/**
 */
public class WrappedException extends RuntimeException  {

    private Throwable throwable;

    public WrappedException( Throwable t ) {
        this.throwable = t;
    }

    public Throwable getCause() {
        return throwable;
    }

}
