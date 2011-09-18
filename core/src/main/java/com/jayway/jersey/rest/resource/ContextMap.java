package com.jayway.jersey.rest.resource;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 */
public class ContextMap {

    private Map<Class, Object> instances = new HashMap<Class, Object>();

    public <T> void put( Class<T> clazz, Object instance ) {
        if ( clazz.isAssignableFrom( instance.getClass() ) ) {
            instances.put( clazz, instance );
        } else {
            throw new IllegalArgumentException("Cannot add role instance as class: "+clazz);
        }
    }

    public <T> T get( Class<T> clazz ) {
        Object instance = instances.get(clazz);
        if ( instance == null ) return null;
        if ( clazz.isAssignableFrom( instance.getClass() ) ) {
            return (T) instance;
        }
        // maybe throw exception. Inconsistent
        return null;
    }
}
