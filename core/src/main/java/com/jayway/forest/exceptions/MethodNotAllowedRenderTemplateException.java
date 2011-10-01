package com.jayway.forest.exceptions;

import com.jayway.forest.reflection.Capability;

/**
 */
public class MethodNotAllowedRenderTemplateException extends RuntimeException {

    private Capability method;

    public MethodNotAllowedRenderTemplateException(Capability method) {
        this.method = method;
    }

    public Capability method() {
        return method;
    }

}
