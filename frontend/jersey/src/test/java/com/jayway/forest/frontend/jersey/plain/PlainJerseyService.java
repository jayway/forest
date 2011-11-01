package com.jayway.forest.frontend.jersey.plain;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.jayway.forest.frontend.jersey.test.StringDTO;

@Path("")
public class PlainJerseyService {
	
	@Path("simpleEcho")
	@GET
	public String simpleEcho(@QueryParam("param") String value) {
		return value;
	}

	@Path("objectEcho")
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	public StringDTO objectEcho(@QueryParam("param") String value) {
		return new StringDTO(value);
	}

}
