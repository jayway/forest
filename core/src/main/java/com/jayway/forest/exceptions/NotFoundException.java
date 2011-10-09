package com.jayway.forest.exceptions;

import javax.servlet.http.HttpServletResponse;

/**
 */
public class NotFoundException extends AbstractHtmlException {
	private static final long serialVersionUID = 1;

    public NotFoundException(String message) {
        super(HttpServletResponse.SC_NOT_FOUND, message);
    }

    public NotFoundException() {
        super(HttpServletResponse.SC_NOT_FOUND, "Not Found" );
    }
}
