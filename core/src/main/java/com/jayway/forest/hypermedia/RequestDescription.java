package com.jayway.forest.hypermedia;

public class RequestDescription {
	private ParameterDescription[] parameters;
	private Link link;

	public RequestDescription(ParameterDescription[] parameters, Link link) {
		this.parameters = parameters;
		this.link = link;
	}

	@SuppressWarnings("unchecked")
	public ParameterDescription getParameter(int i) {
		return parameters[i];
	}
}
