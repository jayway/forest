package com.jayway.forest.reflection.impl;

import com.jayway.forest.reflection.CapabilityReference;
import com.jayway.forest.roles.Linkable;

public class LinkCapabilityReference implements CapabilityReference {
	
	private final Linkable linkable;

	public LinkCapabilityReference(Linkable linkable) {
		this.linkable = linkable;
	}

	@Override
	public String name() {
		return linkable.name();
	}

	@Override
	public String httpMethod() {
		return "GET";
	}

    @Override
    public String href() {
        return linkable.href();
    }

    @Override
    public String rel() {
        return linkable.rel();
    }
}
