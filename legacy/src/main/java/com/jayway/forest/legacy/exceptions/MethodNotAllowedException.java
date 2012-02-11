package com.jayway.forest.legacy.exceptions;


import javax.servlet.http.HttpServletResponse;

/**
 */
public class MethodNotAllowedException extends AbstractHtmlException {
	private static final long serialVersionUID = 1;

    public MethodNotAllowedException(String message) {
        super(HttpServletResponse.SC_METHOD_NOT_ALLOWED, message);
    }

    public MethodNotAllowedException() {
        super(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Method Not Allowed" );
    }
}
