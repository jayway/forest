package com.jayway.forest.reflection;

import com.jayway.forest.roles.Linkable;

public class LinkCapabilityReference implements CapabilityReference {
	
	private final Linkable linkable;

	public LinkCapabilityReference(Linkable linkable) {
		this.linkable = linkable;
	}

	@Override
	public String name() {
		return linkable.href();
	}

	@Override
	public String httpMethod() {
		return "GET";
	}

}
