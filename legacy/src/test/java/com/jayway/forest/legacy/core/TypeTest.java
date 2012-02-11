package com.jayway.forest.legacy.core;

import com.jayway.forest.legacy.service.AbstractRunner;
import com.jayway.forest.legacy.service.StateHolder;

import org.junit.Assert;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class TypeTest extends AbstractRunner {

    @Test
    public void returnFloat() {
        String s = given().expect().statusCode(200).get("/types/getfloat").andReturn().asString();

        Assert.assertEquals( "3.9", s);
    }

    @Test
    public void acceptFloat() {
        given().body("3.5").expect().statusCode(200).post("/types/postfloat");

        Float result = (Float) StateHolder.get();
        Assert.assertEquals( result, new Float(3.5f));
    }

    @Test
    public void acceptFloatForm() {
        given().spec( contentTypeFormUrlEncoded() ).expect().statusCode(200).post("/types/postfloat?argument1=3.875");

        Float result = (Float) StateHolder.get();
        Assert.assertEquals( result, new Float(3.875));
    }

    @Test
    public void testIterable() {

        expect().body("strings.size()", equalTo(1)).
                 body("strings[0].string", equalTo("STRING")).
                 body("integers.size()", equalTo(1)).
                 body("integers[0].integer", equalTo(42)).
        when().get("/types/iterable");
    }

}

