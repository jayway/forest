package com.jayway.jersey.rest.resource.grove;

import com.jayway.forest.grove.RoleManager;
import com.jayway.jersey.rest.resource.ContextMap;

public class GroveContextMap implements ContextMap {
    public <T> T role(Class<T> clazz ) {
    	return RoleManager.role(clazz);
    }
}
