package com.jayway.forest.core;

import com.jayway.forest.core.ForestApplication;
import com.sun.jersey.api.core.PackagesResourceConfig;

public class TestApplication extends ForestApplication {
	public TestApplication() {
		super(new PackagesResourceConfig("com.jayway.forest"));
	}
}
