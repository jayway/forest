package com.jayway.forest.legacy.exceptions;

import javax.servlet.http.HttpServletResponse;

/**
 */
public class UnauthorizedException extends AbstractHtmlException {
	private static final long serialVersionUID = 1;

    public UnauthorizedException(String message) {
        super(HttpServletResponse.SC_UNAUTHORIZED, message);
    }

    public UnauthorizedException() {
        super(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized" );
    }
}
