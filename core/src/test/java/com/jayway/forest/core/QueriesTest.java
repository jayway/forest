package com.jayway.forest.core;

import com.jayway.forest.dto.IntegerDTO;
import com.jayway.forest.dto.StringDTO;
import com.jayway.forest.service.AbstractRunner;
import com.jayway.forest.service.StateHolder;
import org.junit.Assert;
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

    @Test
    public void throwsNotFound() throws IOException {
        given().expect().statusCode(404).body(is("Bad stuff")).get("/throwingnotfound");
    }

    @Test
    public void testQueryWithNoArgument() throws IOException {
        given().expect().statusCode(405).body( is("[0,{\"integer\":0}]") ).when().get("/add");
    }

    @Test
    public void testQueryWithNoArgumentTemplate() throws IOException {
        given().expect().statusCode(405).body( is("[17,{\"integer\":63}]") ).when().get("/addwithtemplates");
    }

    @Test
    public void testQueryWithNoArgumentWrongTemplate() throws IOException {
        given().expect().statusCode(405).body( is("[0,{\"integer\":0}]") ).when().get("/addwithwrongtemplates");
    }

    @Test
    public void testHtmlQueryWithNoArgumentTemplate() throws IOException {
        String contains = "value='Template Content'";
        String form = given().spec(acceptTextHtml()).expect().statusCode(405).when().get("/echo").andReturn().asString();
        Assert.assertTrue("Must have Template Content", form.contains(contains));
    }

    @Test
    public void testHtmlQueryWithNoArgumentComplex() throws IOException {
        String integer = "Integer: <input type='text'  value='17' name='argument1'/>";
        String integerDTO = "integer: <input type='text' value='63' name='argument2.IntegerDTO.integer'/>";
        String form = given().spec(acceptTextHtml()).expect().statusCode(405).when().get("/addwithtemplates").andReturn().asString();
        Assert.assertTrue("Must contain: "+integer, form.contains( integer ));
        Assert.assertTrue("Must contain: "+integerDTO, form.contains(integerDTO));
    }


    @Test
    public void testTemplateWrongType() throws IOException {
        String jsonTemplate = given().expect().statusCode(405).when().get("/withwrongtemplatetype").andReturn().asString();

        Assert.assertTrue("Must be empty json string", jsonTemplate.equals("\"\""));
    }

    @Test
    public void testNonexistentTemplate() throws IOException {
        String jsonTemplate = given().expect().statusCode(405).when().get("/withnonexistingtemplate").andReturn().asString();

        Assert.assertTrue("Must be empty json string", jsonTemplate.equals("\"\""));
    }

    @Test
    public void testTemplateWithArgument() throws IOException {
        String jsonTemplate = given().expect().statusCode(405).when().get("/templatemethodwithargument").andReturn().asString();

        Assert.assertTrue("Must be empty json string", jsonTemplate.equals("\"\""));
    }

}

