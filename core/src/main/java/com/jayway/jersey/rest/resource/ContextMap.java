package com.jayway.jersey.rest.resource;

public interface ContextMap {
    public <T> T role(Class<T> clazz );
}
