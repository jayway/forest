package com.jayway.forest.hypermedia;

public class RequestDescription {
	private final ParameterDescription[] parameters;
	private final Link link;

	public RequestDescription(ParameterDescription[] parameters, Link link) {
		this.parameters = parameters;
		this.link = link;
	}

	public ParameterDescription[] getParameters() {
		return parameters;
	}

	public Link getLink() {
		return link;
	}
	
}
