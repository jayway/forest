package com.jayway.jersey.rest.resource;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.jayway.jersey.rest.service.AbstractRunner;
import com.jayway.jersey.rest.service.RestfulServletService;

/**
 */
public class ConstraintsTest extends AbstractRunner {


    @Test
    public void testConstraint() throws IOException {
    	RestfulServletService.addRole("Hello World", String.class);
        String result = get("/bank/constraint", String.class);
        Assert.assertEquals( "Hello World", result );
    }

    @Test
    public void testIlllegalConstraint() {
        try {
            get( "/bank/constraint", String.class);
            Assert.fail();
        } catch (IOException e) {
            Assert.assertTrue(  e instanceof FileNotFoundException );
        }

    }

/*
    @Test
    public void testIllegalDelete() {
        //mustThrow(webResource.path("test/other/constraint/").accept(MediaType.APPLICATION_JSON), "DELETE", null, 404);
        try {
            delete( "/bank/other/constraint/", String.class);
            Assert.fail();
        } catch (IOException e) {
            Assert.assertTrue(  e instanceof FileNotFoundException );
        }

    }
*/

}
