package com.jayway.forest.core;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.core.MediaType;

import org.junit.Test;

import com.jayway.forest.service.AbstractRunner;
import com.jayway.restassured.response.Response;

public class JavassistTest extends AbstractRunner {
    @Test
    public void noArgQuery() {
        given().
        expect().
        	body(is("Hello world")).
        when().
        	get("/bytecode/noArgQuery");
    }

    @Test
    public void overridePath() {
        given().
        expect().
        	body(is("Hello world")).
        when().
        	get("/bytecode/overriddenPath");
    }

    @Test
    public void overrideHttpMethod() {
        given().
        expect().
        	body(is("Hello world")).
        when().
        	post("/bytecode/doPost");
    }

    @Test
    public void command() {
        given().
        when().
        	put("/bytecode/simplecommand");
        given().
        expect().
        	body(is("1")).
        when().
        	get("/bytecode/getcount");
    }
}
