package com.jayway.forest.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.jayway.forest.core.ResourceUtil;
import com.jayway.forest.di.grove.GroveDependencyInjectionImpl;
import com.jayway.forest.grove.RoleManager;
import com.jayway.forest.roles.Resource;

/**
 */
public class RestfulServletService extends com.jayway.forest.core.RestfulServlet {
	
	public RestfulServletService() {
		super(new GroveDependencyInjectionImpl());
	}

	@Override
	protected Resource root() {
		return new RootResource();
	}

	@Override
	protected void setupContext() {
		RoleManager.clear();
		Set<Entry<Class<?>,Object>> entrySet = map.entrySet();
		for (Entry<Class<?>, Object> entry : entrySet) {
			RoleManager.addRole(entry.getKey(), entry.getValue());
		}
	}
	
	private static Map<Class<?>, Object> map = new HashMap<Class<?>, Object>();

	public static <T> void addRole(T o, Class<T> clazz) {
		map.put(clazz, o);
	}

	public static void reset() {
		map.clear();
	}
}
