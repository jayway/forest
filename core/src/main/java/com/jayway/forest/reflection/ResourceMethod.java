package com.jayway.forest.reflection;

import java.lang.reflect.Method;

import com.jayway.forest.roles.Linkable;

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

    public ResourceMethod(Method method, MethodType type, String documentation) {
		this.method = method;
        this.name = method.getName();
    	this.type = type;
    	this.documentation = documentation;
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

    public Method method() {
        return method;
    }

    public enum MethodType {
        COMMAND, QUERY, SUBRESOURCE, NOT_FOUND, CONSTRAINT_FALSE, ID_RESOURCE
    }

}