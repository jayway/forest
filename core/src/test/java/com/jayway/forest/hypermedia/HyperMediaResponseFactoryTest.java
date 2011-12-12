package com.jayway.forest.hypermedia;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.PUT;

import org.junit.Test;

import com.jayway.forest.constraint.DoNotDiscover;
import com.jayway.forest.roles.Resource;

public class HyperMediaResponseFactoryTest {

	@Test
	public void testCommandQueryMethods() {
		HyperMediaResponse<String> response = HyperMediaResponseFactory.create(MyResource.class).make(new MyResource(), "Hello world", String.class);
		
		Link query = response.linksWithMethod(HttpMethod.GET).get(0);
		assertEquals("getStuff", query.getName());

		Link command = response.linksWithMethod(HttpMethod.POST).get(0);
		assertEquals("doStuff", command.getName());
	}

	public static class MyResource {
		public void doStuff() {
		}
		public String getStuff(int argument) {
			return null;
		}
	}

	@Test
	public void testAnnotatedHttpMethods() {
		HyperMediaResponse<String> response = HyperMediaResponseFactory.create(AnnotatedResource.class).make(new AnnotatedResource(), "Hello world", String.class);
		
		Link annotated = response.linksWithMethod(HttpMethod.PUT).get(0);
		assertEquals("getStuff", annotated.getName());
	}

	public static class AnnotatedResource {
		@PUT
		public String getStuff(int argument) {
			return null;
		}
	}

	@Test
	public void testConstrainedMethod() {
		HyperMediaResponse<String> response = HyperMediaResponseFactory.create(ConstrainedResource.class).make(new ConstrainedResource(), "Hello world", String.class);
		assertEquals(0, response.linksWithMethod(HttpMethod.GET, HttpMethod.POST).size());
	}

	public static class ConstrainedResource implements Resource {
		@DoNotDiscover
		public String getStuff(int argument) {
			return null;
		}
	}
}
