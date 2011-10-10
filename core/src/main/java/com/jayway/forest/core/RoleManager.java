package com.jayway.forest.core;

import com.jayway.forest.di.DependencyInjectionSPI;

/**
 */
public class RoleManager {
    protected static DependencyInjectionSPI spi;

    public static <T> T role(Class<T> clazz) {
        return spi.getRequestContext(clazz);
    }

    public static <T> void addRole(Class<T> clazz, Object instance) {
        if ( clazz.isAssignableFrom( instance.getClass() )) {
            spi.addRequestContext( clazz, (T) instance);
        } else {
            throw new IllegalArgumentException("Object cannot be cast to "+clazz.getSimpleName());
        }
    }
}
