package com.jayway.forest.legacy.exceptions;

import com.jayway.forest.legacy.reflection.Capability;

import javax.servlet.http.HttpServletResponse;

/**
 */
public class MethodNotAllowedRenderTemplateException extends RenderTemplateException {
	private static final long serialVersionUID = 1;

    public MethodNotAllowedRenderTemplateException(Capability method) {
        super(method, HttpServletResponse.SC_METHOD_NOT_ALLOWED );
    }
}
