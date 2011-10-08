package com.jayway.forest.core;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.jayway.forest.service.AbstractRunner;
import com.jayway.forest.service.RestfulServletService;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.get;
import static org.hamcrest.CoreMatchers.is;

/**
 */
public class ConstraintsTest extends AbstractRunner {


    @Test
    public void testConstraint() throws IOException {
    	RestfulServletService.addRole("Hello World", String.class);

        expect().
                body(is("\"Hello World\"")).
        when().
                get("/constraint");
    }

    @Test
    public void testIlllegalConstraint() {
        expect().statusCode(404).when().get("/constraint");
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
