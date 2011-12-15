package com.jayway.forest.hypermedia;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.PUT;

import org.junit.Test;

import com.jayway.forest.constraint.DoNotDiscover;
import com.jayway.forest.roles.CreatableResource;
import com.jayway.forest.roles.IdDiscoverableResource;
import com.jayway.forest.roles.Linkable;
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

	@Test
	public void testCreateable() {
		HyperMediaResponse<String> response = HyperMediaResponseFactory.create(MyCreatableResource.class).make(new MyCreatableResource(), "Hello world", String.class);
		Link annotated = response.linksWithMethod(HttpMethod.PUT).get(0);
		assertEquals("create", annotated.getName());
	}

	public static class MyCreatableResource implements CreatableResource<String>{
		@Override
		public Linkable create(String argument) {
			return null;
		}

		@Override
		public Resource id(String id) {
			return null;
		}
	}

	@Test
	public void testIdDiscoverable() {
		HyperMediaResponse<String> response = HyperMediaResponseFactory.create(MyIdDiscoverableResource.class).make(new MyIdDiscoverableResource(), "Hello world", String.class);
		assertEquals("delete", response.linksWithMethod(HttpMethod.DELETE).get(0).getName());
		assertEquals("posten", response.linksWithMethod(HttpMethod.POST).get(0).getName());
	}

	public static class MyIdDiscoverableResource implements IdDiscoverableResource {
		@Override
		public Resource id(String id) {
			return null;
		}

		@Override
		public List<Link> discover() {
			return Arrays.asList(new Link("uri", HttpMethod.DELETE, "delete", "documentation"),
					new Link("uri", HttpMethod.POST, "posten", "documentation"));
		}
	}
}
