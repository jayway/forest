package com.jayway.forest.reflection.impl;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Map;

import com.jayway.forest.core.MediaTypeHandler;
import com.jayway.forest.exceptions.CreatedException;
import com.jayway.forest.exceptions.MethodNotAllowedRenderTemplateException;
import com.jayway.forest.roles.CreatableResource;
import com.jayway.forest.roles.Linkable;
import com.jayway.forest.roles.Resource;

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
