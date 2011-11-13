package com.jayway.forest.core;

import com.jayway.forest.di.grove.GroveDependencyInjectionImpl;

public class Setup {
	public static void setup() {
		RoleManager.spi = new GroveDependencyInjectionImpl();
	}
}
