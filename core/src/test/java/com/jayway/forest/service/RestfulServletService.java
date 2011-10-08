package com.jayway.forest.service;

import com.jayway.forest.core.Application;
import com.jayway.forest.core.RoleManager;
import com.jayway.forest.di.grove.GroveDependencyInjectionImpl;
import com.jayway.forest.roles.Resource;
import com.jayway.forest.servlet.RestfulServlet;

import javax.servlet.ServletException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 */
public class RestfulServletService extends RestfulServlet {
	private static final long serialVersionUID = 1;

    @Override
    public void init() throws ServletException {
        super.init();
        initForest(new Application() {
            @Override
            public Resource root() {
                return new RootResource();
            }

            @Override
            public void setupRequestContext() {
                RoleManager.clear();
                Set<Entry<Class<?>,Object>> entrySet = map.entrySet();
                for (Entry<Class<?>, Object> entry : entrySet) {
                    RoleManager.addRole(entry.getKey(), entry.getValue());
                }
            }

        }, new GroveDependencyInjectionImpl());
    }

    private static Map<Class<?>, Object> map = new HashMap<Class<?>, Object>();

	public static <T> void addRole(T o, Class<T> clazz) {
		map.put(clazz, o);
	}

	public static void reset() {
		map.clear();
	}
}
