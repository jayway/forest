package com.jayway.forest.reflection.impl;

import com.jayway.forest.core.MediaTypeHandler;
import com.jayway.forest.exceptions.CreatedException;
import com.jayway.forest.exceptions.MethodNotAllowedRenderTemplateException;
import com.jayway.forest.reflection.RestReflection;
import com.jayway.forest.roles.CreatableResource;
import com.jayway.forest.roles.Resource;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Map;

public class CapabilityCreateCommand extends CapabilityCommand {

    private CreatableResource creatable;

    public CapabilityCreateCommand( Method method, CreatableResource resource, String documentation ) {
		super( method, resource, documentation, resource.getClass().getSimpleName()+"Create");
        this.creatable = resource;
	}

    @Override
	protected <T extends Resource> void invokeCommand(Object... arguments) {
        throw new CreatedException( creatable.create( arguments[0] ) );
    }

    @Override
    public String httpMethod() {
        return "POST";
    }

    @Override
    public void post(Map<String, String[]> formParams, InputStream stream, MediaTypeHandler mediaTypeHandler) {
         super.put(formParams, stream, mediaTypeHandler);
    }

    @Override
    public void put(Map<String, String[]> formParams, InputStream stream, MediaTypeHandler mediaTypeHandler) {
        throw new MethodNotAllowedRenderTemplateException( this );
    }

    @Override
    public Object renderForm(RestReflection restReflection) {
        return restReflection.renderCommandCreateForm( this );
    }
}
