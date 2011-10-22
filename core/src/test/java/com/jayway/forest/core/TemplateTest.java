package com.jayway.forest.core;

import com.jayway.forest.service.AbstractRunner;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static com.jayway.restassured.RestAssured.given;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.is;

public class TemplateTest extends AbstractRunner {

    @Test
    public void testTemplate() throws IOException {
        String json = given().expect().statusCode(405).when().get("/templates/updatewithtemplate").andReturn().as(String.class);

        assertEquals("Template Content", json);
    }

    @Test
    public void testCommandSimple() throws IOException {
        String json = given().expect().statusCode(405).when().get("/templates/command").andReturn().as(String.class);
        assertEquals("", json);
    }


    @Test
    public void testQueryWithNoArgument() throws IOException {
        given().expect().statusCode(405).body( is("[0,{\"integer\":0}]") ).when().get("/templates/add");
    }

    @Test
    public void testQueryWithNoArgumentTemplate() throws IOException {
        given().expect().statusCode(405).body( is("[17,{\"integer\":63}]") ).when().get("/templates/addwithtemplates");
    }

    @Test
    public void testQueryWithNoArgumentWrongTemplate() throws IOException {
        given().expect().statusCode(405).body( is("[0,{\"integer\":0}]") ).when().get("/templates/addwithwrongtemplates");
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
        String jsonTemplate = given().expect().statusCode(405).when().get("/templates/withwrongtemplatetype").andReturn().asString();

        Assert.assertTrue("Must be empty json string", jsonTemplate.equals("\"\""));
    }

    @Test
    public void testNonexistentTemplate() throws IOException {
        String jsonTemplate = given().expect().statusCode(405).when().get("/templates/withnonexistingtemplate").andReturn().asString();

        Assert.assertTrue("Must be empty json string", jsonTemplate.equals("\"\""));
    }

    @Test
    public void testTemplateWithArgument() throws IOException {
        String jsonTemplate = given().expect().statusCode(405).when().get("/templates/templatemethodwithargument").andReturn().asString();

        Assert.assertTrue("Must be empty json string", jsonTemplate.equals("\"\""));
    }

}

