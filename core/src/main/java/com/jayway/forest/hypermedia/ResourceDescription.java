package com.jayway.forest.hypermedia;

import java.util.LinkedList;
import java.util.List;

public class ResourceDescription<T> {
	
	public final String title;
	private List<Link> links = new LinkedList<Link>();
	private final T body;
	private final Class<T> bodyClass;

	public ResourceDescription(String title, T body, Class<T> bodyClass) {
		this.title = title;
		this.body = body;
		this.bodyClass = bodyClass;
	}
	
	public void addLink(Link link) {
		links.add(link);
	}

	public void addLinks(List<Link> links) {
		this.links.addAll(links);
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((body == null) ? 0 : body.hashCode());
		result = prime * result + ((links == null) ? 0 : links.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("unchecked")
		ResourceDescription<T> other = (ResourceDescription<T>) obj;
		if (body == null) {
			if (other.body != null)
				return false;
		} else if (!body.equals(other.body))
			return false;
		if (links == null) {
			if (other.links != null)
				return false;
		} else if (!links.equals(other.links))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}
}
