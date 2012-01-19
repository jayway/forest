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

	public String getDefaultValue() {
		if (defaultValue == null) {
			return "";
		}
		return defaultValue.toString();
	}
}
