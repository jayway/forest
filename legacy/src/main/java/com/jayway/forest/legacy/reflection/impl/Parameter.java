package com.jayway.forest.legacy.reflection.impl;

/**
 */
public class Parameter {
    private Class<?> clazz;
    private Object template;
    private String name;

    public Parameter(int indx, Class<?> clazz ) {
        this.name = "argument" + (indx + 1);
        this.clazz = clazz;
    }

    public Class<?> parameterCls() {
        return clazz;
    }

    public Object getTemplate() {
        return template;
    }
    public void setTemplate( Object template ) {
        this.template = template;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
