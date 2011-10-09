package com.jayway.forest.exceptions;

import javax.servlet.http.HttpServletResponse;

/**
 */
public class InternalServerErrorException extends AbstractHtmlException {
	private static final long serialVersionUID = 1;

    public InternalServerErrorException(String message) {
        super(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
    }

    public InternalServerErrorException() {
        super(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error" );
    }
}
