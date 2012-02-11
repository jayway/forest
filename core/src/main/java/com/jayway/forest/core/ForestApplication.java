package com.jayway.forest.core;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class ForestApplication extends Application {
	
	private final Application delegate;
	private final Set<Class<?>> delegateClasses;
	private final Set<Object> delegateSingletons;
	private final ForestProxyFactory proxyFactory = new ForestProxyFactory();
	
	public ForestApplication(Application delegate) throws Exception {
		this.delegate = delegate;
		delegateClasses = getDelegateClasses();
		delegateSingletons = getDelegateSingletons();
	}
	
	@Override
	public Set<Class<?>> getClasses() {
		return delegateClasses;
	}

	@Override
	public Set<Object> getSingletons() {
		return delegateSingletons;
	}

	private Set<Object> getDelegateSingletons() throws Exception {
		Set<Object> classes = delegate.getSingletons();
		Set<Object> result = new HashSet<Object>();
		for (Object object : classes) {
			result.add(proxyFactory.proxy(object));
		}
		return result;
	}

	private Set<Class<?>> getDelegateClasses() {
		Set<Class<?>> classes = delegate.getClasses();
		Set<Class<?>> result = new HashSet<Class<?>>();
		for (Class<?> clazz : classes) {
			try {
				result.add(proxyFactory.getProxyClass(clazz));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return result;
	}
}
