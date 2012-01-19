package com.jayway.forest.core;

import static com.jayway.restassured.RestAssured.given;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.jayway.forest.dto.Value;
import com.jayway.forest.service.AbstractRunner;
import com.jayway.forest.service.StateHolder;

@Ignore
public class JSONParsingTest extends AbstractRunner {

    @Test
    public void testCommandSimple() {
        String body = "Hello";
        given().body( body ).expect().statusCode(400).when().post("/command");
    }


    @Test
    public void testCommandAcceptsEnum() {
        String body = "\"One\"";
        given().body( body ).expect().statusCode(200).when().post("/commandenum");

        Value value = (Value) StateHolder.get();
        Assert.assertEquals( value, Value.One );
    }


}

