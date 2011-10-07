package com.jayway.forest.core;

import com.jayway.forest.dto.IntegerDTO;
import com.jayway.forest.dto.StringDTO;
import com.jayway.forest.service.AbstractRunner;
import com.jayway.forest.service.StateHolder;

import org.junit.Test;

import java.io.IOException;

import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;

public class QueriesTest  extends AbstractRunner {

    @Test
    public void testInvokeIndex() throws IOException {
        StateHolder.set("Expected String");

        StringDTO returned = get("/other/description").as(StringDTO.class);
        assertEquals("Expected String", returned.string());
    }

    @Test
    public void testQueryWithInteger() throws IOException {
        final IntegerDTO integer = given().param("argument1", "60").when().get("/addten").andReturn().as(IntegerDTO.class);

        assertEquals(70, integer.getInteger().intValue());
    }

    @Test
    public void testQueryWithIntegerWrongInput() throws IOException {
        given().
                queryParam( "argument1", "x6f?0").
        expect().
                statusCode(405).
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
}
