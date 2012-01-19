package com.jayway.forest.core;

import com.jayway.forest.dto.IntegerDTO;
import com.jayway.forest.dto.StringDTO;
import com.jayway.forest.service.AbstractRunner;
import com.jayway.forest.service.StateHolder;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;

@Ignore
public class QueriesTest  extends AbstractRunner {

    @Test
    public void testInvokeIndex() throws IOException {
        StateHolder.set("Expected String");

        StringDTO returned = get("/other/read").as(StringDTO.class);
        assertEquals("Expected String", returned.string());
    }

    @Test
    public void testQueryWithInteger() throws IOException {
        final IntegerDTO integer = given().param("argument1", "60").when().get("/addten").andReturn().as(IntegerDTO.class);

        assertEquals(70, integer.getInteger().intValue());
    }

    @Test
    public void testQueryWithNamedParam() throws IOException {
        final IntegerDTO integer = given().param("paramWithName", "60").when().get("/namedaddten").andReturn().as(IntegerDTO.class);

        assertEquals(70, integer.getInteger().intValue());
    }

    @Test
    public void testQueryWithIntegerWrongInput() throws IOException {
        given().
                queryParam( "argument1", "x6f?0").
        expect().
                statusCode(400).
        when().
                get("/addten").statusLine();
    }
    @Test
    public void testQueryWithIntegerWrongInput2() throws IOException {
        given().
                queryParam( "arent1", "2").
        expect().
                statusCode(400).
        when().
                get("/addten").statusLine();
    }



    @Test
    public void testQueryWithIntegerAndInteger() throws IOException {
        given().
                queryParam("argument1", "60").
                queryParam("argument2.IntegerDTO.integer", "13").
        expect().
                body(is("73")).
        when().
                get("/add");
    }

    @Test
    public void throwsNotFound() throws IOException {
        given().expect().statusCode(404).body(is("Bad stuff")).get("/throwingnotfound");
    }

    @Test
    public void escapedStrings() {
        StateHolder.set( new StringDTO("Man shouts :\"My GOD http://localhost?!!\""));
        String result = given().expect().statusCode(200).get("/getstring").andReturn().asString();

        System.out.println( result );
        Assert.assertEquals("{\"string\":\"Man shouts :\\\"My GOD http:\\/\\/localhost?!!\\\"\"}", result );
    }

}

