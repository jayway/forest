package com.jayway.jersey.rest.service;

import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.JerseyTest;
import org.junit.Assert;
import org.junit.Before;

/**
 */
public class AbstractRunner extends JerseyTest {

    public AbstractRunner() throws Exception {
        super( "com.jayway.jersey.rest.service" );
    }

    @Before
    public void setup() {
        StateHolder.set(null);
    }

    /**
     * Move to abstract class that all tests extends
     */
    protected void mustThrow( WebResource.Builder builder, String method, Class<?> argument, int errorCode ) {
        try {
            if ( argument == null ) {
                builder.method( method );
            } else {
                builder.method( method, argument );
            }
            //builder.get( argument );
            Assert.fail("Must throw exception");
        } catch( UniformInterfaceException e) {
            Assert.assertEquals( errorCode, e.getResponse().getStatus() );
        }
    }

}
