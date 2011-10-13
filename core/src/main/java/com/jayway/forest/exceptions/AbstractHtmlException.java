package com.jayway.forest.exceptions;

import java.util.*;

/**
 */
abstract public class AbstractHtmlException extends RuntimeException {

    public static Map<Integer, String> messageMapping;

	static {
		Map<Integer, String> map = new HashMap<Integer, String>();
        map.put( 400, "Bad Request");
        map.put( 409, "Conflict");
        map.put( 401, "Unauthorized");
        map.put( 500, "Internal Server Error");
        map.put( 404, "Not Found");
        map.put( 405, "Method Not Allowed");
        map.put( 415, "Unsupported Media Type");
        AbstractHtmlException.messageMapping = Collections.unmodifiableMap(map);
    }

    private int code;
    private String message;

    public AbstractHtmlException( int code, String message ) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
