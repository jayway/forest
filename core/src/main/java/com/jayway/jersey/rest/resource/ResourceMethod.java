package com.jayway.jersey.rest.resource;

import com.jayway.jersey.rest.roles.Linkable;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ResourceMethod {

    private MethodType type = MethodType.NOT_FOUND;
    private Method method;
    private String name;
    private String documentation;

    public ResourceMethod(Linkable link) {
		name = link.id();
        type = MethodType.SUBRESOURCE;
    }
    
    public ResourceMethod() {
	}

    // TODO: this should be replaced with a factory method
    public ResourceMethod(ResourceUtil resourceUtil, Resource resource, Method method) {
		this.method = method;
        this.name = method.getName();

        if (Modifier.isAbstract(method.getModifiers())) return;
        if (!Modifier.isPublic(method.getModifiers())) return;
        if (!resourceUtil.checkConstraint(resource, method)) {
            type = MethodType.CONSTRAINT_FALSE;
            return;
        }
        documentation = resourceUtil.getDocumentation(method);
        handleReturnType(method.getReturnType());
    }

    private void handleReturnType(Class<?> returnType) {
        if (returnType.equals(Void.TYPE)) {
            type = MethodType.COMMAND;
        } else if (Resource.class.isAssignableFrom(returnType)) {
            if (method.getParameterTypes().length == 0) type = MethodType.SUBRESOURCE;
            else type = MethodType.ID_RESOURCE;
        } else {
            type = MethodType.QUERY;
        }
    }

    public MethodType type() {
        return type;
    }

    public String name() {
        return name;
    }

    public boolean isCommand() {
        return type == MethodType.COMMAND;
    }

    public boolean isQuery() {
        return type == MethodType.QUERY;
    }

    public boolean isSubResource() {
        return type == MethodType.SUBRESOURCE;
    }

    public boolean isNotFound() {
        return type == MethodType.NOT_FOUND;
    }

    public boolean isConstraintFalse() {
        return type == MethodType.CONSTRAINT_FALSE;
    }

    public boolean isIdSubResource() {
        return type == MethodType.ID_RESOURCE;
    }

    public String documentation() {
        return documentation;
    }

    public boolean isDocumented() {
        return documentation != null;
    }

    protected Method method() {
        return method;
    }

    public enum MethodType {
        COMMAND, QUERY, SUBRESOURCE, NOT_FOUND, CONSTRAINT_FALSE, ID_RESOURCE
    }

}