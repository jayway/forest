package com.jayway.forest.exceptions;

import com.jayway.forest.reflection.Capability;

/**
 */
public class MethodNotAllowedRenderTemplateException extends RuntimeException {
	private static final long serialVersionUID = 1;

    private Capability method;

    public MethodNotAllowedRenderTemplateException(Capability method) {
        this.method = method;
    }

    public Capability method() {
        return method;
    }

}
