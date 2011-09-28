package com.jayway.forest.grove;

import java.util.HashMap;
import java.util.Map;

public class RoleManager {
    private static ThreadLocal<Map<Class<?>, Object>> roleMap = new ThreadLocal<Map<Class<?>, Object>>() {
    	@Override
    	protected Map<Class<?>, Object> initialValue() {
    		return new HashMap<Class<?>, Object>();
    	}
    };
	
    @SuppressWarnings("unchecked")
	public static <T> T context(Class<T> clazz) {
    	return (T) roleMap.get().get(clazz);
    }

    public static <T> void addToContext(Class<T> clazz, Object instance) {
    	// TODO: check that role does not exists
    	roleMap.get().put(clazz, instance);
    }
    
    public static void clear() {
    	roleMap.remove();
    }
}
