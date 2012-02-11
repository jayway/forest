package com.jayway.forest.legacy.exceptions;

import javax.servlet.http.HttpServletResponse;

/**
 */
public class UnsupportedMediaTypeException extends AbstractHtmlException {
	private static final long serialVersionUID = 1;

    public UnsupportedMediaTypeException(String message) {
        super(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, message);
    }

    public UnsupportedMediaTypeException() {
        super(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Unsupported Media Type");
    }
}
