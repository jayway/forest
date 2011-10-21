package com.jayway.forest.core;

import com.jayway.forest.service.AbstractRunner;
import com.jayway.forest.service.StateHolder;
import org.junit.Assert;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.given;

public class TypeTest extends AbstractRunner {

    @Test
    public void returnFloat() {
        String s = given().expect().statusCode(200).get("/getfloat").andReturn().asString();

        Assert.assertEquals( "3.9", s);
    }

    @Test
    public void acceptFloat() {
        given().body("3.5").expect().statusCode(200).post("/postfloat");

        Float result = (Float) StateHolder.get();
        Assert.assertEquals( result, new Float(3.5f));
    }

    @Test
    public void acceptFloatForm() {
        //given().spec( contentTypeFormUrlEncoded() ).formParam("argument1.Float", "3.5").expect().statusCode(200).post("/postfloat");
    }

}

