package com.jayway.forest.frontend.jersey.plain;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

public class PlainSubResource {
	
	private final String name;

	public PlainSubResource(String name) {
		this.name = name;
	}

	@Path("echo")
	@GET
	public String simpleEcho(@QueryParam("param") String value) {
		return name + ":" + value;
	}
}
