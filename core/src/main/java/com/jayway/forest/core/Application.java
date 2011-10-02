package com.jayway.forest.core;

import com.jayway.forest.reflection.Transformer;
import com.jayway.forest.roles.Resource;

import java.util.Map;

public interface Application {
	Resource root();
    void setupRequestContext();
    Map<Class, Transformer> transformers();
}
