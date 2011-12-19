package com.jayway.forest.hypermedia;

public class ParameterDescription {
	private final String name;
	private final Object defaultValue;

	public ParameterDescription(String name, Object defaultValue) {
		this.name = name;
		this.defaultValue = defaultValue;
	}

	public String getName() {
		return name;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}
}
