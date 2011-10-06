package com.jayway.forest.core;

import com.jayway.forest.roles.Resource;

public interface Application {
	Resource root();
    void setupRequestContext();
}
