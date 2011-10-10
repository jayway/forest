package com.jayway.forest.reflection.impl;

import com.jayway.forest.exceptions.UnsupportedMediaTypeException;
import com.jayway.forest.reflection.Capabilities;
import com.jayway.forest.reflection.RestReflection;

import java.lang.reflect.Method;

/**
 */
public class AtomRestReflection implements RestReflection {

    public static final AtomRestReflection INSTANCE = new AtomRestReflection();
    private AtomRestReflection() {}

    @Override
    public Object renderListResponse(PagedSortedListResponse responseObject) {
        // TODO use a velocity template
        return null;
    }

    @Override
    public Object renderQueryResponse(Object responseObject) {
        throw new UnsupportedMediaTypeException();
    }

    @Override
    public Object renderCapabilities(Capabilities capabilities) {
        throw new UnsupportedMediaTypeException();
    }

    @Override
    public Object renderCommandForm(Method method) {
        throw new UnsupportedMediaTypeException();
    }

    @Override
    public Object renderQueryForm(Method method) {
        throw new UnsupportedMediaTypeException();
    }

}
