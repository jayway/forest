package com.jayway.forest.samples.bank.simple;

import com.jayway.forest.core.ForestApplication;
import com.sun.jersey.api.core.PackagesResourceConfig;

public class SimpleApplication extends ForestApplication {
	public SimpleApplication() throws Exception {
		super(new PackagesResourceConfig("com.jayway.forest.samples.bank.simple", "com.jayway.forest.writers.html"));
	}
	
}
