package com.jayway.forest.legacy.exceptions;

import com.jayway.forest.legacy.reflection.Capability;

import javax.servlet.http.HttpServletResponse;

/**
 */
public class BadRequestRenderTemplateException extends RenderTemplateException {
	private static final long serialVersionUID = 1;

    public BadRequestRenderTemplateException(Capability capability) {
        super(capability, HttpServletResponse.SC_BAD_REQUEST );
    }
}
