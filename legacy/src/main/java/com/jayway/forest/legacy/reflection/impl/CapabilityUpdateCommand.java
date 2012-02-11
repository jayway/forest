package com.jayway.forest.legacy.reflection.impl;

import com.jayway.forest.legacy.core.MediaTypeHandler;
import com.jayway.forest.legacy.exceptions.CreatedException;
import com.jayway.forest.legacy.exceptions.MethodNotAllowedRenderTemplateException;
import com.jayway.forest.legacy.roles.CreatableResource;
import com.jayway.forest.legacy.roles.Linkable;
import com.jayway.forest.legacy.roles.Resource;
import com.jayway.forest.legacy.roles.UpdatableResource;

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
