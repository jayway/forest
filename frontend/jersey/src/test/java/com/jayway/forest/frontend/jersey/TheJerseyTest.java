package com.jayway.forest.frontend.jersey;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.parsing.Parser.JSON;
import static org.hamcrest.CoreMatchers.is;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.testing.ServletTester;

import com.jayway.restassured.RestAssured;

public class TheJerseyTest {
    private static ServletTester tester;

	@BeforeClass
    public static void initServletContainer () throws Exception
    {
        tester = new ServletTester();
        tester.setContextPath("/app");
        ServletHolder servlet = tester.addServlet(com.sun.jersey.spi.container.servlet.ServletContainer.class, "/*");
        servlet.setInitParameter("com.sun.jersey.config.property.packages", "com.jayway.forest.frontend.jersey.test");
        servlet.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature", "true");
        RestAssured.baseURI = tester.createSocketConnector(true);
        RestAssured.defaultParser = JSON;
        RestAssured.basePath = "/app";
        RestAssured.requestContentType("application/json");
        tester.start();
    }

    /**
     * Stops the Jetty container.
     */
    @AfterClass
    public static void cleanupServletContainer () throws Exception
    {
        tester.stop();
        RestAssured.reset();
    }

    @Test
    public void expectAddToWork() {
        given().
    		queryParam("argument1", "60").
    		queryParam("argument2.IntegerDTO.integer", "13").
        expect().
        	body(is("73")).
        when().
        	get("/add");
    }

    @Test
    public void objectsCanBeReturnedAsJSON() {
        given().
    		queryParam("argument1", "hello").
        expect().
        	body("string", is("hello")).
        when().
        	get("/echo");
    }

    @Test
    @Ignore("does not work")
    public void post() {
        given().
    		formParam("argument1", "stored secret").
        when().
        	post("/save");
        expect().
        	body(is("stored secret")).
        when().
        	get("/load");
    }
}
