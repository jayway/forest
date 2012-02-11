package com.jayway.forest.legacy.reflection.impl;

import com.jayway.forest.legacy.reflection.CapabilityReference;
import com.jayway.forest.legacy.roles.Linkable;

public class CapabilityLinkable implements CapabilityReference {
	
	private final Linkable linkable;

	public CapabilityLinkable(Linkable linkable) {
		this.linkable = linkable;
	}

	@Override
	public String name() {
		return linkable.getName();
	}

	@Override
	public String httpMethod() {
		return "GET";
	}

    @Override
    public String uri() {
        return linkable.getUri();
    }

    @Override
    public String rel() {
        return linkable.getRel();
    }
}
