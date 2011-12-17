package com.jayway.forest.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;

import com.jayway.forest.Sneak;
import com.jayway.forest.constraint.ConstraintViolationException;
import com.jayway.forest.constraint.DoNotDiscover;
import com.jayway.forest.hypermedia.HyperMediaResponse;
import com.jayway.forest.hypermedia.HyperMediaResponseFactory;
import com.jayway.forest.roles.ReadableResource;
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

		@DoNotDiscover
		public String constrained() {
			return "ERROR";
		}
	}

	@Test(expected=ConstraintViolationException.class)
	public void resourceConstraint() throws Exception {
		ForestProxyFactory proxyFactory = new ForestProxyFactory();
		NoArgResource original = new NoArgResource();
		Object proxy = proxyFactory.proxy(original);
		query(proxy, "constrained");
	}

	public static class MyReadableResource implements ReadableResource<String> {
		@Override
		public String read() {
			return "hello";
		}
	}
	@Test
	public void hypermedia() throws Exception {
		ForestProxyFactory proxyFactory = new ForestProxyFactory();
		MyReadableResource original = new MyReadableResource();
		Object proxy = proxyFactory.proxy(original);
		Object result = query(proxy, ForestProxyFactory.FOREST_GET_HYPERMEDIA);
		HyperMediaResponse<String> expected = HyperMediaResponseFactory.create(MyReadableResource.class).make(original, original.read(), String.class);
		assertEquals(expected, result);
	}

	private Object query(Object proxy, String methodName) throws NoSuchMethodException, IllegalAccessException {
		Method method = proxy.getClass().getMethod(methodName, null);
		try {
			return method.invoke(proxy, null);
		} catch (InvocationTargetException e) {
			return Sneak.sneakyThrow(e.getCause());
		}
	}

}
