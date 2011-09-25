package com.jayway.jersey.rest.service;

import com.jayway.jersey.rest.RestfulServlet;
import com.jayway.jersey.rest.resource.Resource;

/**
 */
public class RestfulServletService extends com.jayway.jersey.rest.RestfulServlet {

    @Override
    protected String servletMapping() {
        return "/bank";
    }

    @Override
    protected Resource root() {
        return new RootResource();
    }

    @Override
    protected void setupContext() {
        if ( StateHolder.get() != null ) {
            if ( StateHolder.get() instanceof String ) {
                getContextMap().put( String.class, StateHolder.get());
            }
        }
    }
}
