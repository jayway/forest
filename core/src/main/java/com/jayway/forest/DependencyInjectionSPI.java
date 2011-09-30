package com.jayway.forest;

public interface DependencyInjectionSPI {
	<T> void addRequestContext(Class<T> clazz, T object);
	<T> T postCreate(T object);
}
