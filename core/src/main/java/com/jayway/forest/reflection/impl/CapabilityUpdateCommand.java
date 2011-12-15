package com.jayway.forest.reflection.impl;

import com.jayway.forest.core.MediaTypeHandler;
import com.jayway.forest.exceptions.CreatedException;
import com.jayway.forest.exceptions.MethodNotAllowedRenderTemplateException;
import com.jayway.forest.roles.CreatableResource;
import com.jayway.forest.roles.Linkable;
import com.jayway.forest.roles.Resource;
import com.jayway.forest.roles.UpdatableResource;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Map;

public class CapabilityUpdateCommand extends CapabilityCommand {

    private UpdatableResource updatable;

    public CapabilityUpdateCommand(Method method, UpdatableResource resource, String documentation) {
		super( method, resource, documentation , "update" );
        this.updatable = resource;
	}

    @Override
	protected <T extends Resource> void invokeCommand(Object... arguments) {
        updatable.update(arguments[0]);
    }

    @Override
    public String httpMethod() {
        return "PUT";
    }

    @Override
    public void put(Map<String, String[]> formParams, InputStream stream, MediaTypeHandler mediaTypeHandler) {
        super.post(formParams, stream, mediaTypeHandler);
    }

    @Override
    public void post(Map<String, String[]> formParams, InputStream stream, MediaTypeHandler mediaTypeHandler) {
        if ( mediaTypeHandler.acceptHtml() ) {
            super.post(formParams, stream, mediaTypeHandler);
        } else {
            throw new MethodNotAllowedRenderTemplateException( this );
        }
    }
}
