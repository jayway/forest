package com.jayway.restfuljersey.samples.bank.jersey.resources;

import java.util.LinkedList;
import java.util.List;

public class HyperMediaResponse<T> {
	
	public final String title;
	private List<Link> links = new LinkedList<Link>();
	private final T body;
	private final Class<T> bodyClass;

	public HyperMediaResponse(String title, T body, Class<T> bodyClass) {
		this.title = title;
		this.body = body;
		this.bodyClass = bodyClass;
	}
	
	public void addLink(Link link) {
		links.add(link);
	}

	public String getTitle() {
		return title;
	}

	public List<Link> linksWithMethod(String... httpMethods) {
		List<Link> result = new LinkedList<Link>();
		for (Link link : links) {
			for (String httpMethod : httpMethods) {
				if (link.getHttpMethod().equals(httpMethod)) {
					result.add(link);
					break;
				}
			}
		}
		return result;
	}

	public T getBody() {
		return body;
	}

	public Class<T> getBodyClass() {
		return bodyClass;
	}
}
