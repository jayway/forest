package com.jayway.forest.reflection;

import com.jayway.forest.roles.Linkable;

/**
 */
public interface Transformer<T> {
    Linkable transform(T t);
}
