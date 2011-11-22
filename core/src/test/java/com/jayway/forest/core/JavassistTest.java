package com.jayway.forest.core;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import org.junit.Before;
import org.junit.Test;

import com.jayway.forest.service.AbstractRunner;
import com.jayway.forest.service.ByteCodeResource;

public class JavassistTest extends AbstractRunner {
	
	@Before
	public void before() {
		// FIXME: reset does not work since we are not actually calling this class but the copied class instead
		ByteCodeResource.reset();
	}
	
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
        	put("/bytecode/incCommand");
        given().
        expect().
        	body(is("1")).
        when().
        	get("/bytecode/getcount");
    }

    @Test
    public void commandWithArgument() {
        given().
        	body("10").
        expect().
        	statusCode(204).
        when().
        	put("/bytecode/add");
        given().
        expect().
        	body(is("10")).
        when().
        	get("/bytecode/getcount");
    }
}
