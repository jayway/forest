package com.jayway.forest.core;

import com.jayway.forest.service.AbstractRunner;
import com.jayway.restassured.RestAssured;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

/**
 */
public class JSONApiTest extends AbstractRunner {

    private String baseUrl() {
        return RestAssured.baseURI + RestAssured.basePath;
    }

    @Test
    public void commandGet() {
        expect().statusCode( 405 ).
                body("method", equalTo("PUT")).
                body("href", equalTo( baseUrl() + "/command")).
                body("jsonTemplate", equalTo("")).when().get("/command");
    }

    @Test
    public void commandPost() {
        expect().statusCode( 405 ).
                body("method", equalTo("PUT")).
                body("href", equalTo( baseUrl() + "/command")).
                body("jsonTemplate", equalTo("")).when().post("/command");
    }

    @Test
    public void commandDelete() {
        expect().statusCode(405).
                body("method", equalTo("PUT")).
                body("href", equalTo( baseUrl() + "/command")).
                body("jsonTemplate", equalTo("")).when().delete("/command");
    }


    @Test
    public void queryPut() {
        expect().statusCode( 405 ).
                body("method", equalTo("GET")).
                body("href", equalTo( baseUrl() + "/echo")).
                body("jsonTemplate", equalTo("")).when().put("/echo");
    }

    @Test
    public void queryPost() {
        expect().statusCode( 405 ).
                body("method", equalTo("GET")).
                body("href", equalTo( baseUrl() + "/echo")).
                body("jsonTemplate", equalTo("")).when().post("/echo");
    }

    @Test
    public void queryDelete() {
        expect().statusCode( 405 ).
                body("method", equalTo("GET")).
                body("href", equalTo( baseUrl() + "/echo")).
                body("jsonTemplate", equalTo("")).when().delete("/echo");
    }


    @Test
    public void commandCreateGet() {
        expect().statusCode(405).
                body("method", equalTo("POST")).
                body("href", equalTo( baseUrl() + "/other/create")).
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
                body("href", equalTo( baseUrl() + "/other/create")).
                body("jsonTemplate.string", equalTo("")).
                body("jsonTemplate.integer", equalTo(0)).
                when().put("/other/create");
    }

    @Test
    public void commandCreateDelete() {
        expect().statusCode( 405 ).
                body("method", equalTo("POST")).
                body("href", equalTo( baseUrl() + "/other/create")).
                body("jsonTemplate.string", equalTo("")).
                body("jsonTemplate.integer", equalTo(0)).
                when().delete("/other/create");
    }


    @Test
    public void commandDeleteGet() {
        expect().statusCode(405).
                body("method", equalTo("DELETE")).
                body("href", equalTo( baseUrl() + "/other/delete")).
                when().get("/other/delete");
    }

    @Test
    public void commandDeletePut() {
        expect().statusCode( 405 ).
                body("method", equalTo("DELETE")).
                body("href", equalTo( baseUrl() + "/other/delete")).
                when().put("/other/delete");
    }

    @Test
    public void commandDeletePost() {
        expect().statusCode( 405 ).
                body("method", equalTo("DELETE")).
                body("href", equalTo( baseUrl() + "/other/delete")).
                when().post("/other/delete");
    }


    @Test
    public void encodingTest() {
        String value = "Übercoolness æøåôõ";
        given().param("argument1", value ).expect().statusCode(200).body(is("\""+value+"\"")).when().get("/echo");
    }
}
