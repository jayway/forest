package com.jayway.forest.di.grove;

import com.jayway.forest.DependencyInjectionSPI;
import com.jayway.forest.grove.RoleManager;

public class GroveDependencyInjectionImpl implements DependencyInjectionSPI {

	@Override
	public <T> void addRequestContext(Class<T> clazz, T object) {
		RoleManager.addRole(clazz, object);
	}

	@Override
	public <T> T postCreate(T object) {
		return object;
	}

}
