package com.jayway.forest.exceptions;

import com.jayway.forest.reflection.Capability;

import javax.servlet.http.HttpServletResponse;

/**
 */
public class MethodNotAllowedRenderTemplateException extends AbstractHtmlException {
	private static final long serialVersionUID = 1;

    private Capability method;

    public MethodNotAllowedRenderTemplateException(Capability method) {
        super(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "" );
        this.method = method;
    }

    public Capability method() {
        return method;
    }

}
