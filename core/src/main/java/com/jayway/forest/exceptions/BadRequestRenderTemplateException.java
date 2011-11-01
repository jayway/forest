package com.jayway.forest.exceptions;

import com.jayway.forest.reflection.Capability;

import javax.servlet.http.HttpServletResponse;

/**
 */
public class BadRequestRenderTemplateException extends RenderTemplateException {
	private static final long serialVersionUID = 1;

    public BadRequestRenderTemplateException(Capability capability) {
        super(capability, HttpServletResponse.SC_BAD_REQUEST );
    }
}
