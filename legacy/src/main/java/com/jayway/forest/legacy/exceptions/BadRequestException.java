package com.jayway.forest.legacy.exceptions;

import javax.servlet.http.HttpServletResponse;

/**
 */
public class BadRequestException extends AbstractHtmlException  {
	private static final long serialVersionUID = 1;

    public BadRequestException() {
        super(HttpServletResponse.SC_BAD_REQUEST, "Bad Request" );
    }

    public BadRequestException( String message ) {
        super( HttpServletResponse.SC_BAD_REQUEST, message );
    }
}
