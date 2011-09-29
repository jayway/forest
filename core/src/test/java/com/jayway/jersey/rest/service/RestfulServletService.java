package com.jayway.jersey.rest.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.jayway.forest.grove.RoleManager;
import com.jayway.jersey.rest.resource.Resource;
import com.jayway.jersey.rest.resource.ResourceUtil;
import com.jayway.jersey.rest.resource.grove.GroveContextMap;

/**
 */
public class RestfulServletService extends com.jayway.jersey.rest.RestfulServlet {
	private final ResourceUtil resourceUtil = new ResourceUtil(new GroveContextMap());

	@Override
	protected Resource root() {
		return new RootResource();
	}

	@Override
	protected void setupContext() {
		RoleManager.clear();
		Set<Entry<Class<?>,Object>> entrySet = map.entrySet();
		for (Entry<Class<?>, Object> entry : entrySet) {
			RoleManager.addToContext(entry.getKey(), entry.getValue());
		}
	}
	
	private static Map<Class<?>, Object> map = new HashMap<Class<?>, Object>();

	public static <T> void addRole(T o, Class<T> clazz) {
		map.put(clazz, o);
	}

	public static void reset() {
		map.clear();
	}

	@Override
	protected ResourceUtil resourceUtil() {
		return resourceUtil;
	}
}
