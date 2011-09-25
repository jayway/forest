package com.jayway.jersey.rest.resource;

import com.jayway.jersey.rest.service.AbstractRunner;
import com.jayway.jersey.rest.service.StateHolder;
import org.junit.Assert;
import org.junit.Test;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 */
public class ConstraintsTest extends AbstractRunner {


    @Test
    public void testConstraint() throws IOException {
        StateHolder.set("Hello World");
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
