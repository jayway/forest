package com.jayway.forest.core;

import com.jayway.forest.service.AbstractRunner;
import com.jayway.forest.service.StateHolder;
import com.jayway.restassured.RestAssured;
import org.junit.Assert;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.given;

/**
 */
public class CreatableTest extends AbstractRunner {

    @Test
    public void testCreate() {
        given().body("{\"string\":\"Hello\", \"integer\": 25}").
        expect().statusCode( 201 ).header("Location", RestAssured.baseURI + RestAssured.basePath + "/other/Hello/" + 25 + "/")
                .when().post("/other/");
    }

    @Test
    public void testDelete() {
        given().expect().statusCode(200).delete("/other/");

        Assert.assertEquals( "Delete invoked", StateHolder.get() );
    }

}
