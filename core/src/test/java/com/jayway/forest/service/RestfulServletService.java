package com.jayway.forest.service;

import com.jayway.forest.core.Application;
import com.jayway.forest.core.RoleManager;
import com.jayway.forest.di.grove.GroveDependencyInjectionImpl;
import com.jayway.forest.roles.Resource;
import com.jayway.forest.servlet.ExceptionMapper;
import com.jayway.forest.servlet.Response;
import com.jayway.forest.servlet.RestfulServlet;

import javax.naming.OperationNotSupportedException;
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

    @Override
    protected ExceptionMapper exceptionMapper() {
        return new ExceptionMapper() {
            @Override
            public Response map(Exception e) {
                if ( e instanceof OperationNotSupportedException ) {
                    return new Response( 409, "Not yet supported" );
                } else if ( e instanceof NullPointerException ) {
                    return new Response( 409, "NPE is mapped");
                }

                // could not map
                return null;
            }
        };
    }
}
