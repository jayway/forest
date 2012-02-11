package com.jayway.forest.writers;

import org.apache.velocity.app.VelocityEngine;

public class VelocityEngineHolder {
	
	private VelocityEngine engine;

	public VelocityEngineHolder() {
		this.engine = new VelocityEngine();
        engine.setProperty("resource.loader","class");
        engine.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        try {
			engine.init();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public VelocityEngine get() {
		return engine;
	}

}
