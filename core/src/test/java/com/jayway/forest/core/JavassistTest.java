package com.jayway.forest.core;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;

import com.jayway.forest.service.AbstractRunner;
import com.jayway.forest.service.ByteCodeResource;

public class JavassistTest extends AbstractRunner {
	
	@Before
	public void before() {
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

    @Test
    public void commandWithArgumentDTO() {
        given().
        	body("{\"integer\": 10}").
        expect().
        	statusCode(204).
        when().
        	put("/bytecode/adddto");
        given().
        expect().
        	body(is("10")).
        when().
        	get("/bytecode/getcount");
    }

    @Test
    public void commandTwoWithArguments() {
        given().
        	formParam("argument1", "5").formParam("argument2", "2").
        	contentType(MediaType.APPLICATION_FORM_URLENCODED).
        expect().
        	statusCode(204).
        when().
        	put("/bytecode/addMultiple");
        given().
        expect().
        	body(is("10")).
        when().
        	get("/bytecode/getcount");
    }

    @Test
    public void commandWithTwoArgumentBody() {
        given().
        	body("[2, 5]").
        expect().
        	statusCode(204).
        when().
        	put("/bytecode/addMultiple");
        given().
        expect().
        	body(is("10")).
        when().
        	get("/bytecode/getcount");
    }

    @Test
    public void queryWithArgument() {
        given().
        	queryParam("argument1", "qwe").
        expect().
    	body(is("qwe")).
        when().
        	get("/bytecode/echo");
    }
}
