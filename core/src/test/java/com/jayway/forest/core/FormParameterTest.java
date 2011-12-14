package com.jayway.forest.core;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.jayway.forest.service.AbstractRunner;
import com.jayway.forest.service.StateHolder;

/**
 */
public class FormParameterTest extends AbstractRunner {

    @Test
    public void testFormParams() throws IOException {
    	given().
    		formParam("argument1", "hello").
    		contentType(MediaTypeHandler.FORM_URL_ENCODED).
        expect().
                statusCode(200).
                body(containsString("Operation completed successfully")).
        when().
                post("/command");

        assertThat(StateHolder.get().toString(), equalTo("hello"));
    }

    @Test
    public void testNamedFormParams() throws IOException {
    	given().
    		formParam("theName", "helloagain").
    		contentType(MediaTypeHandler.FORM_URL_ENCODED).
        expect().
                statusCode(200).
                body(containsString("Operation completed successfully")).
        when().
                post("/commandwithnamedparam");

        assertThat(StateHolder.get().toString(), equalTo("helloagain"));
    }

    @Test
    public void htmlInputFieldNames() {
        String s = given().spec(acceptTextHtml()).get("/command").asString();
        Assert.assertTrue("Input field must be called argument1", s.contains("<input type='text' name='argument1'/>"));
    }

    @Test
    public void htmlInputFieldNamesWithFormParam() {
        String s = given().spec(acceptTextHtml()).get("/commandwithnamedparam").asString();
        Assert.assertTrue("Input field must be called theName", s.contains("<input type='text' name='theName'/>"));
    }

}
