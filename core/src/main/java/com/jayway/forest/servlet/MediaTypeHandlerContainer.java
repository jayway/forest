package com.jayway.forest.servlet;

import java.util.HashMap;
import java.util.Map;

import com.jayway.forest.reflection.RestReflection;

public class MediaTypeHandlerContainer {
    private static Map<String, RestReflection> reflectors = new HashMap<String, RestReflection>();

    public RestReflection restReflection(String mediaType) {
        return reflectors.get( mediaType );
    }

	public void setHandler(String mediaType, RestReflection restReflection) {
		reflectors.put(mediaType, restReflection);
	}

}
