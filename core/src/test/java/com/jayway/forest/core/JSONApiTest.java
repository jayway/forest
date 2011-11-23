package com.jayway.forest.core;

import com.jayway.forest.service.AbstractRunner;
import com.jayway.forest.service.StateHolder;
import com.jayway.restassured.RestAssured;
import org.junit.Assert;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.parsing.Parser.JSON;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

/**
 */
public class JSONApiTest extends AbstractRunner {

    private String baseUrl() {
        return RestAssured.baseURI + RestAssured.basePath;
    }

    @Test
    public void encodingTest() {
        String value = "Übercoolness æøåôõ";
        given().param("argument1", value ).expect().statusCode(200).body(is("\""+value+"\"")).when().get("/echo");
    }

    @Test
    public void encodingTest2() {
        String value = "Übercoolness æøåôõ";
        given().body("\"" + value + "\"").expect().statusCode(200).when().put("/command");
        String result = (String) StateHolder.get();
        Assert.assertEquals( value, result );
    }
}
