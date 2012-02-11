package com.jayway.forest.legacy.exceptions;

import javax.servlet.http.HttpServletResponse;

/**
 */
public class ConflictException extends AbstractHtmlException {
	private static final long serialVersionUID = 1;

    public ConflictException() {
        super( HttpServletResponse.SC_CONFLICT, "Conflict" );
    }

    public ConflictException( String message ) {
        super( HttpServletResponse.SC_CONFLICT, message );
    }
}
