package com.jayway.forest.di;

public interface DependencyInjectionSPI {
	<T> void addRequestContext(Class<T> clazz, T object);
	<T> T postCreate(T object);
    <T> T getRequestContext(Class<T> clazz);
    void clear();
}
