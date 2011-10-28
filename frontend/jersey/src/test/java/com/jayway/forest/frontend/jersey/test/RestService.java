package com.jayway.forest.frontend.jersey.test;

import javax.ws.rs.Path;

import com.jayway.forest.core.Application;
import com.jayway.forest.di.grove.GroveDependencyInjectionImpl;
import com.jayway.forest.frontend.jersey.ForestJerseyService;
import com.jayway.forest.roles.Resource;

@Path("")
public class RestService extends ForestJerseyService {
	
	public RestService() {
		super(new Application() {
			@Override
			public void setupRequestContext() {
			}
			
			@Override
			public Resource root() {
				return new RootResource();
			}
		}, new GroveDependencyInjectionImpl());
		System.out.println("------------ RestService");
	}
	
}
