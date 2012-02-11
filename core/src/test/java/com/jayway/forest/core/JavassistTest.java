package com.jayway.forest.core;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.jayway.forest.dto.IntegerDTO;
import com.jayway.forest.service.AbstractRunner;
import com.jayway.forest.service.ByteCodeResource;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.response.ResponseBody;

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
        	post("/bytecode/incCommand");
        given().
        expect().
        	body(is("1")).
        when().
        	get("/bytecode/getcount");
    }

    @Test
    public void commandWithArgument() {
        given().
        	formParam("argument1", "10").
        	contentType(MediaType.APPLICATION_FORM_URLENCODED).
        expect().
        	statusCode(200).
        when().
        	post("/bytecode/add");
        given().
        expect().
        	body(is("10")).
        when().
        	get("/bytecode/getcount");
    }

    @Test
    public void commandWithJsonBody() {
        given().
        	body("{\"integer\": 10}").
        expect().
        	statusCode(200).
        when().
        	post("/bytecode/adddto");
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
        	statusCode(200).
        when().
        	post("/bytecode/adddto");
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
        	statusCode(200).
        when().
        	post("/bytecode/addMultiple");
        given().
        expect().
        	body(is("10")).
        when().
        	get("/bytecode/getcount");
    }

    @Test
    @Ignore("This is not supported!")
    public void commandWithTwoArgumentBody() {
        given().
        	body("[2, 5]").
        expect().
        	statusCode(204).
        when().
        	post("/bytecode/addMultiple");
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

    @Test
    public void hypermediaResponse() throws Exception {
        Response response = given().
        expect().
        	contentType(MediaType.TEXT_HTML).
        when().
        	get("/bytecode/");
        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void descriptionResponse() throws Exception {
        Response response = given().
        expect().
        	contentType(MediaType.TEXT_HTML).
        when().
        	get("/bytecode/addMultiple");
        System.out.println(response.getBody().asString());
        assertEquals(405, response.getStatusCode());
    }
}
