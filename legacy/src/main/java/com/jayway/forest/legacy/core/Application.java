package com.jayway.forest.legacy.core;

import com.jayway.forest.legacy.roles.Resource;

public interface Application {
	Resource root();
    void setupRequestContext();
}
