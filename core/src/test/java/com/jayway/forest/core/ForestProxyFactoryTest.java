package com.jayway.forest.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;

import com.jayway.forest.roles.Resource;

public class ForestProxyFactoryTest {

	@Test
	public void resourceWithConstructor() throws Exception {
		ForestProxyFactory proxyFactory = new ForestProxyFactory();
		MyResource original = new MyResource("hello");
		Object proxy = proxyFactory.proxy(original);
		assertFalse(original.equals(proxy));
		assertEquals("hello", query(proxy, "getMessage"));
	}

	public static class MyResource implements Resource {
		private final String message;

		public MyResource(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}
	}

	@Test
	public void resourceWithNoArgConstructor() throws Exception {
		ForestProxyFactory proxyFactory = new ForestProxyFactory();
		NoArgResource original = new NoArgResource();
		Object proxy = proxyFactory.proxy(original);
		assertFalse(original.equals(proxy));
		assertEquals(1, query(proxy, "inc"));
		assertEquals(2, query(proxy, "inc"));
	}

	public static class NoArgResource implements Resource {
		private int count;

		public int inc() {
			return ++count;
		}
	}

	private Object query(Object proxy, String methodName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Method method = proxy.getClass().getMethod(methodName, null);
		return method.invoke(proxy, null);
	}
	
}
