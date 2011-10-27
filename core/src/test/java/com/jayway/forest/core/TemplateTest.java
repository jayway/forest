package com.jayway.forest.core;

import com.jayway.forest.service.AbstractRunner;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static com.jayway.restassured.RestAssured.given;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

public class TemplateTest extends AbstractRunner {

    @Test
    public void testTemplate() throws IOException {
        given().expect().statusCode(405).
                body("jsonTemplate", equalTo("Template Content")).when().get("/templates/updatewithtemplate");
    }

    @Test
    public void testCommandSimple() throws IOException {
        given().expect().statusCode(405).
                body("jsonTemplate", equalTo("")).when().get("/templates/command");
    }


    @Test
    public void testQueryWithNoArgument() throws IOException {
        given().expect().statusCode(405).
                body( "jsonTemplate[0]", equalTo(0) ).
                body( "jsonTemplate[1].integer", equalTo(0) ).
                when().get("/templates/add");
    }

    @Test
    public void testQueryWithNoArgumentTemplate() throws IOException {
        given().expect().statusCode(405).
                body( "jsonTemplate[0]", equalTo(17) ).
                body( "jsonTemplate[1].integer", equalTo(63) ).
                when().get("/templates/addwithtemplates");
    }

    @Test
    public void testQueryWithNoArgumentWrongTemplate() throws IOException {
        given().expect().statusCode(405).
                body( "jsonTemplate[0]", equalTo(0) ).
                body( "jsonTemplate[1].integer", equalTo(0) ).
                when().get("/templates/addwithwrongtemplates");
    }

    @Test
    public void testHtmlQueryWithNoArgumentTemplate() throws IOException {
        String contains = "value='Template Content'";
        String form = given().spec(acceptTextHtml()).expect().statusCode(405).when().get("/templates/echo").andReturn().asString();
        Assert.assertTrue("Must have Template Content", form.contains(contains));
    }

    @Test
    public void testHtmlQueryWithNoArgumentComplex() throws IOException {
        String integer = "Integer: <input type='text'  value='17' name='argument1'/>";
        String integerDTO = "integer: <input type='text' value='63' name='argument2.IntegerDTO.integer'/>";
        String form = given().spec(acceptTextHtml()).expect().statusCode(405).when().get("/templates/addwithtemplates").andReturn().asString();
        Assert.assertTrue("Must contain: "+integer, form.contains( integer ));
        Assert.assertTrue("Must contain: "+integerDTO, form.contains(integerDTO));
    }


    @Test
    public void testTemplateWrongType() throws IOException {
        given().expect().statusCode(405).
                body( "jsonTemplate", equalTo("")).
                when().get("/templates/withwrongtemplatetype");
    }

    @Test
    public void testNonexistentTemplate() throws IOException {
        given().expect().statusCode(405).
                body( "jsonTemplate", equalTo("")).
                when().get("/templates/withnonexistingtemplate");
    }

    @Test
    public void testTemplateWithArgument() throws IOException {
        given().expect().statusCode(405).
                body( "jsonTemplate", equalTo("")).
                when().get("/templates/templatemethodwithargument");
    }

}

