package com.jayway.jersey.rest.exceptions;

import com.jayway.jersey.rest.resource.ResourceMethod;

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
