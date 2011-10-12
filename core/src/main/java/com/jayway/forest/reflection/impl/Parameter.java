package com.jayway.forest.reflection.impl;

/**
 */
public class Parameter {
    private Class<?> clazz;
    private Object template;

    public Parameter( Class<?> clazz ) {
        this.clazz = clazz;
    }

    public Parameter( Class<?> clazz, Object template) {
        this.clazz = clazz;
        this.template = template;
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
}
