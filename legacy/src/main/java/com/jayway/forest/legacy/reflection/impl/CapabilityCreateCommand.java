package com.jayway.forest.legacy.reflection.impl;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Map;

import com.jayway.forest.legacy.core.MediaTypeHandler;
import com.jayway.forest.legacy.exceptions.CreatedException;
import com.jayway.forest.legacy.exceptions.MethodNotAllowedRenderTemplateException;
import com.jayway.forest.legacy.roles.CreatableResource;
import com.jayway.forest.legacy.roles.Linkable;
import com.jayway.forest.legacy.roles.Resource;

public class CapabilityCreateCommand extends CapabilityCommand {

    private CreatableResource creatable;

    public CapabilityCreateCommand( Method method, CreatableResource resource, String documentation ) {
		super( method, resource, documentation , "create" );
        this.creatable = resource;
	}

    @Override
	protected <T extends Resource> void invokeCommand(Object... arguments) {
        Linkable linkable = creatable.create(arguments[0]);
        linkable.setRel( "child" );
        throw new CreatedException( linkable );
    }

    @Override
    public String httpMethod() {
        return "POST";
    }
}
