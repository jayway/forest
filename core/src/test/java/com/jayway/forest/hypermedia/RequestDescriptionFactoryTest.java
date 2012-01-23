package com.jayway.forest.hypermedia;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;

import org.junit.Test;

import com.jayway.forest.roles.Template;

public class RequestDescriptionFactoryTest {

	@Test
	public void primitiveArguments() throws Exception {
		RequestDescription description = RequestDescriptionFactory.create(MyResource.class).make(new MyResource(), "getStuff");
		assertEquals(1, description.getParameters().length);
		assertEquals("argument1", description.getParameters()[0].getName());
		assertEquals("", description.getParameters()[0].getDefaultValue());
	}

	@Test
	public void primitiveArgumentsDefaultValue() throws Exception {
		RequestDescription description = RequestDescriptionFactory.create(MyResource.class).make(new MyResource(), "getStuffWithDefault");
		assertEquals("5", description.getParameters()[0].getDefaultValue());
	}

	@Test
	public void primitiveArgumentsTemplate() throws Exception {
		RequestDescription description = RequestDescriptionFactory.create(MyResource.class).make(new MyResource(), "getStuffWithTemplate");
		assertEquals("19", description.getParameters()[0].getDefaultValue());
	}

	@Test
	public void namedArgumentQueryParam() throws Exception {
		RequestDescription description = RequestDescriptionFactory.create(MyResource.class).make(new MyResource(), "someWithQueryParam");
		assertEquals("name", description.getParameters()[0].getName());
	}

	@Test
	public void namedArgumentFormParam() throws Exception {
		RequestDescription description = RequestDescriptionFactory.create(MyResource.class).make(new MyResource(), "someWithFormParam");
		assertEquals("name-form", description.getParameters()[0].getName());
	}

	public static class MyResource {
		public String getStuff(int argument) {
			return null;
		}
		public String getStuffWithDefault(@DefaultValue("5") int argument) {
			return null;
		}
		int argumentTemplate() {
			return 19;
		}
		public String getStuffWithTemplate(@Template("argumentTemplate") int argument) {
			return null;
		}

		public String someWithQueryParam(@QueryParam("name") String name) {
			return null;
		}

		public String someWithFormParam(@FormParam("name-form") String name) {
			return null;
		}
	}

}
