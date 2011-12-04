package com.jayway.forest.core;

import com.sun.jersey.api.core.PackagesResourceConfig;

public class TestApplication extends ForestApplication {
	public TestApplication() throws Exception {
		super(new PackagesResourceConfig("com.jayway.forest"));
	}
	
}
