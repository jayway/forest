package com.jayway.restfuljersey.samples.bank.jersey.resources;


public class Link {

	private final String uri;
	private final String httpMethod;
	private final String name;
	private final String documentation;

	public Link(String uri, String httpMethod, String name, String documentation) {
		this.uri = uri;
		this.httpMethod = httpMethod;
		this.name = name;
		this.documentation = documentation;
	}

	public String getUri() {
		return uri;
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public String getName() {
		return name;
	}

	public String getDocumentation() {
		return documentation;
	}
}
