package com.jayway.forest.hypermedia;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.PUT;

import org.junit.Test;

import com.jayway.forest.constraint.DoNotDiscover;
import com.jayway.forest.roles.CreatableResource;
import com.jayway.forest.roles.DeletableResource;
import com.jayway.forest.roles.IdDiscoverableResource;
import com.jayway.forest.roles.Linkable;
import com.jayway.forest.roles.ReadUpdatableResource;
import com.jayway.forest.roles.Resource;

public class HyperMediaResponseFactoryTest {

	@Test
	public void testCommandQueryMethods() {
		ResourceDescription<String> response = ResourceDescriptionFactory.create(MyResource.class).make(new MyResource(), "Hello world", String.class);
		
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
		ResourceDescription<String> response = ResourceDescriptionFactory.create(AnnotatedResource.class).make(new AnnotatedResource(), "Hello world", String.class);
		
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
		ResourceDescription<String> response = ResourceDescriptionFactory.create(ConstrainedResource.class).make(new ConstrainedResource(), "Hello world", String.class);
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
		ResourceDescription<String> response = ResourceDescriptionFactory.create(MyCrudResource.class).make(new MyCrudResource(), "Hello world", String.class);
		assertEquals("create", response.linksWithMethod(HttpMethod.PUT).get(0).getName());
		assertEquals("read", response.linksWithMethod(HttpMethod.GET).get(0).getName());
		assertEquals("update", response.linksWithMethod(HttpMethod.POST).get(0).getName());
		assertEquals("delete", response.linksWithMethod(HttpMethod.DELETE).get(0).getName());
	}

	public static class MyCrudResource implements CreatableResource<String>, ReadUpdatableResource<String>, DeletableResource {
		@Override
		public Linkable create(String argument) {
			return null;
		}

		@Override
		public Resource id(String id) {
			return null;
		}

		@Override
		public String read() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void update(String argument) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void delete() {
			// TODO Auto-generated method stub
			
		}
	}

	@Test
	public void testIdDiscoverable() {
		ResourceDescription<String> response = ResourceDescriptionFactory.create(MyIdDiscoverableResource.class).make(new MyIdDiscoverableResource(), "Hello world", String.class);
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
