package com.jayway.forest.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;

import org.junit.Test;

import com.jayway.forest.Sneak;
import com.jayway.forest.constraint.ConstraintViolationException;
import com.jayway.forest.constraint.DoNotDiscover;
import com.jayway.forest.dto.StringDTO;
import com.jayway.forest.hypermedia.HyperMediaResponse;
import com.jayway.forest.hypermedia.HyperMediaResponseFactory;
import com.jayway.forest.hypermedia.RequestDescription;
import com.jayway.forest.roles.ReadableResource;
import com.jayway.forest.roles.Resource;
import com.jayway.forest.roles.Template;

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

	public static class ResourceMethodsWithArguments implements Resource {
		public String noDefault(String arg) {
			return "hello";
		}

		public String withDefault(@DefaultValue("qwe") String arg) {
			return "hello";
		}

		public String withTemplate(@Template("qwe") String arg) {
			return "hello";
		}
		
		String qwe() {
			return "qwe-template";
		}

		public String namedParam(@QueryParam("param") String arg) {
			return "hello";
		}

		public String primitiveParam(int arg) {
			return "hello";
		}

		public void update(@FormParam("arg") String arg) {
		}

		public void multiple(@FormParam("arg") String arg, @FormParam("other") String other) {
		}

		// TODO: test this
		public void body(StringDTO dto) {
		}
	}

	@Test
	public void requestDescriptionArgumentNames() throws Exception {
		ForestProxyFactory proxyFactory = new ForestProxyFactory();
		ResourceMethodsWithArguments original = new ResourceMethodsWithArguments();
		Object proxy = proxyFactory.proxy(original);
		assertEquals("argument1", ((RequestDescription) query(proxy, "noDefault_description")).getParameters()[0].getName());
		assertEquals("param", ((RequestDescription) query(proxy, "namedParam_description")).getParameters()[0].getName());
		assertEquals("arg", ((RequestDescription) query(proxy, "update_description")).getParameters()[0].getName());
		assertEquals("other", ((RequestDescription) query(proxy, "multiple_description")).getParameters()[1].getName());
	}

	@Test
	public void requestDescriptionLink() throws Exception {
		ForestProxyFactory proxyFactory = new ForestProxyFactory();
		ResourceMethodsWithArguments original = new ResourceMethodsWithArguments();
		Object proxy = proxyFactory.proxy(original);
		RequestDescription description = (RequestDescription) query(proxy, "noDefault_description");
		assertEquals("noDefault", description.getLink().getName());
	}

	@Test
	public void requestDescriptionDefaultValues() throws Exception {
		ForestProxyFactory proxyFactory = new ForestProxyFactory();
		ResourceMethodsWithArguments original = new ResourceMethodsWithArguments();
		Object proxy = proxyFactory.proxy(original);
		assertEquals("qwe", ((RequestDescription) query(proxy, "withDefault_description")).getParameters()[0].getDefaultValue());
		assertEquals("qwe-template", ((RequestDescription) query(proxy, "withTemplate_description")).getParameters()[0].getDefaultValue());
	}

	private Object query(Object proxy, String methodName) throws Exception {
		Method method = proxy.getClass().getMethod(methodName, null);
		try {
			return method.invoke(proxy, null);
		} catch (InvocationTargetException e) {
			return Sneak.sneakyThrow(e.getCause());
		}
	}

}
