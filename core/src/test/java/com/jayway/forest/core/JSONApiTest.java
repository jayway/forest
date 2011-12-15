package com.jayway.forest.core;

import com.jayway.forest.service.AbstractRunner;
import com.jayway.forest.service.StateHolder;
import com.jayway.restassured.RestAssured;
import org.junit.Assert;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

/**
 */
public class JSONApiTest extends AbstractRunner {

    private String baseUrl() {
        return baseURI + basePath;
    }

    @Test
    public void commandGet() {
        expect().statusCode( 405 ).
                body("method", equalTo("POST")).
                body("uri", equalTo( baseUrl() + "/command")).
                body("jsonTemplate", equalTo("")).when().get("/command");
    }

    /*@Test
    public void commandPut() {
        expect().statusCode( 405 ).
                body("method", equalTo("POST")).
                body("uri", equalTo( baseUrl() + "/command")).
                body("jsonTemplate", equalTo("")).when().put("/command");
    }*/

    @Test
    public void commandDelete() {
        expect().statusCode(405).
                body("method", equalTo("POST")).
                body("uri", equalTo( baseUrl() + "/command")).
                body("jsonTemplate", equalTo("")).when().delete("/command");
    }

    @Test
    public void queryGet() {
        // wrong parameters
        expect().statusCode(400).
        body("method", equalTo("GET")).
        body("uri", equalTo( baseUrl() + "/echo")).
        body("jsonTemplate", equalTo("")).when().get("/echo");

    }

    @Test
    public void queryPut() {
        expect().statusCode( 405 ).
                body("method", equalTo("GET")).
                body("uri", equalTo( baseUrl() + "/echo")).
                body("jsonTemplate", equalTo("")).when().put("/echo");
    }

    @Test
    public void queryPost() {
        expect().statusCode( 405 ).
                body("method", equalTo("GET")).
                body("uri", equalTo( baseUrl() + "/echo")).
                body("jsonTemplate", equalTo("")).when().post("/echo");
    }

    @Test
    public void queryDelete() {
        expect().statusCode( 405 ).
                body("method", equalTo("GET")).
                body("uri", equalTo( baseUrl() + "/echo")).
                body("jsonTemplate", equalTo("")).when().delete("/echo");
    }


    @Test
    public void commandCreateGet() {
        expect().statusCode(405).
                body("method", equalTo("POST")).
                body("uri", equalTo( baseUrl() + "/other/")).
                body("jsonTemplate.string", equalTo("")).
                body("jsonTemplate.integer", equalTo(0)).
                when().get("/other/create");
    }

    @Test
    public void commandCreatePut() {
        expect().statusCode(405).
                when().put("/other/");
    }

    @Test
    public void commandCreatePutDirect() {
        expect().statusCode( 405 ).
                body("method", equalTo("POST")).
                body("uri", equalTo( baseUrl() + "/other/")).
                body("jsonTemplate.string", equalTo("")).
                body("jsonTemplate.integer", equalTo(0)).
                when().put("/other/create");
    }

    @Test
    public void commandCreateDelete() {
        expect().statusCode( 405 ).
                body("method", equalTo("POST")).
                body("uri", equalTo( baseUrl() + "/other/")).
                body("jsonTemplate.string", equalTo("")).
                body("jsonTemplate.integer", equalTo(0)).
                when().delete("/other/create");
    }


    @Test
    public void commandDeleteGet() {
        expect().statusCode(405).
                body("method", equalTo("DELETE")).
                body("uri", equalTo( baseUrl() + "/other/")).
                when().get("/other/delete");
    }

    @Test
    public void commandDeletePut() {
        expect().statusCode( 405 ).
                body("method", equalTo("DELETE")).
                body("uri", equalTo( baseUrl() + "/other/")).
                when().put("/other/delete");
    }

    @Test
    public void commandDeletePost() {
        expect().statusCode( 405 ).
                body("method", equalTo("DELETE")).
                body("uri", equalTo( baseUrl() + "/other/")).
                when().post("/other/delete");
    }


    @Test
    public void encodingTest() {
        String value = "Übercoolness æøåôõ";
        given().param("argument1", value ).expect().statusCode(200).body(is("\""+value+"\"")).when().get("/echo");
    }

    @Test
    public void encodingTest2() {
        String value = "Übercoolness æøåôõ";
        given().body( "\""+value+"\"").expect().statusCode(200).when().post("/command");
        String result = (String) StateHolder.get();
        Assert.assertEquals( value, result );
    }


    @Test
    public void createCommandTest() {
        expect().statusCode(200).
                body("links.method[2]", equalTo("POST")).
                body("links.rel[2]", equalTo("create")).
                body("links.uri[2]", equalTo(baseUrl() + "/other/")).when().get("/other/");
    }

    @Test
    public void createCommandTest2() {
        String result = given().spec(acceptTextHtml()).get("/other/").asString();

        Assert.assertTrue("Must have the create link", result.contains( baseUrl() + "/other/create" ));
        Assert.assertTrue("Must have the delete link", result.contains( baseUrl() + "/other/delete" ));
    }

    @Test
    public void deleteCommandTest() {
        expect().statusCode(200).
                body("links.method[1]", equalTo("DELETE")).
                body("links.rel[1]", equalTo("delete")).
                body("links.uri[1]", equalTo( baseUrl() + "/other/")).when().get("/other/");
    }


}
