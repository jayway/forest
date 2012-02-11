package com.jayway.forest.legacy.di.grove;

import com.jayway.forest.legacy.di.DependencyInjectionSPI;

import java.util.HashMap;
import java.util.Map;

public class GroveDependencyInjectionImpl implements DependencyInjectionSPI {

    // this should be the old RoleManager implementation
    private static ThreadLocal<Map<Class<?>, Object>> roleMap = new ThreadLocal<Map<Class<?>, Object>>() {
    	@Override
    	protected Map<Class<?>, Object> initialValue() {
    		return new HashMap<Class<?>, Object>();
    	}
    };

	@Override
	public <T> void addRequestContext(Class<T> clazz, T object) {
		roleMap.get().put(clazz, object);
	}

    @Override
	public <T> T postCreate(T object) {
		return object;
	}

    @Override
    public <T> T getRequestContext(Class<T> clazz) {
        return (T) roleMap.get().get(clazz);
    }

    @Override
    public void clear() {
        roleMap.remove();
    }

}
