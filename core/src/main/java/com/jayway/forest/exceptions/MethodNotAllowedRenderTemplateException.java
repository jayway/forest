package com.jayway.forest.exceptions;

import com.jayway.forest.reflection.Capability;

import javax.servlet.http.HttpServletResponse;

/**
 */
public class MethodNotAllowedRenderTemplateException extends RenderTemplateException {
	private static final long serialVersionUID = 1;

    public MethodNotAllowedRenderTemplateException(Capability method) {
        super(method, HttpServletResponse.SC_METHOD_NOT_ALLOWED );
    }
}
