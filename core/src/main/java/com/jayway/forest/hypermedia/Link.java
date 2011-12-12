package com.jayway.forest.hypermedia;

import javax.ws.rs.core.MediaType;


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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((documentation == null) ? 0 : documentation.hashCode());
		result = prime * result + ((httpMethod == null) ? 0 : httpMethod.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
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
		Link other = (Link) obj;
		if (documentation == null) {
			if (other.documentation != null)
				return false;
		} else if (!documentation.equals(other.documentation))
			return false;
		if (httpMethod == null) {
			if (other.httpMethod != null)
				return false;
		} else if (!httpMethod.equals(other.httpMethod))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}
}
