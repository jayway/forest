package com.jayway.forest.samples.bank.jersey.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.jayway.forest.hypermedia.HyperMediaResponse;
import com.sun.jersey.spi.resource.Singleton;

@Path("sub")
@Singleton
public class SubClassResource extends RootResource {
	@GET
	public HyperMediaResponse<String> root() {
		return super.root();
	}
}
