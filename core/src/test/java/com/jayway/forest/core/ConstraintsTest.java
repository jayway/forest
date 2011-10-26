package com.jayway.forest.core;

import com.jayway.forest.service.AbstractRunner;
import com.jayway.forest.service.RestfulServletService;
import org.junit.Test;

import java.io.IOException;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;
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
        expect().statusCode(401).when().get("/constraint");
    }

    @Test
    public void testIlllegalConstraintHtml() {
        given().spec(acceptTextHtml()).expect().statusCode(401).when().get("/constraint");
    }


    @Test
    public void testIllegalDelete() {
        given().expect().statusCode( 405 ).when().delete("/other/contraint/");
    }

}
