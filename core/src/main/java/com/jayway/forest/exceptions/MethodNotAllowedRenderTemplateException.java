package com.jayway.forest.exceptions;

import com.jayway.forest.reflection.ResourceMethod;

/**
 */
public class MethodNotAllowedRenderTemplateException extends RuntimeException {

    private ResourceMethod method;

    public MethodNotAllowedRenderTemplateException(ResourceMethod method) {
        this.method = method;
    }

    public ResourceMethod method() {
        return method;
    }

}
