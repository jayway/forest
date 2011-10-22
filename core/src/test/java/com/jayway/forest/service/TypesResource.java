package com.jayway.forest.service;

import com.jayway.forest.roles.Resource;

/**
 */
public class TypesResource implements Resource {

    public Float getfloat() {
        return 3.9f;
    }

    public void postfloat( Float f ) {
        StateHolder.set( f );
    }

}
