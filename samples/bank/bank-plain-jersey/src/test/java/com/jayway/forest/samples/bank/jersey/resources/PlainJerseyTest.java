package com.jayway.forest.samples.bank.jersey.resources;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.parsing.Parser.JSON;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.core.MediaType;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.testing.ServletTester;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

public class PlainJerseyTest {
    private static ServletTester tester;

	@BeforeClass
    public static void initServletContainer () throws Exception
    {
        tester = new ServletTester();
        tester.setContextPath("/app");
        ServletHolder servlet = tester.addServlet(com.sun.jersey.spi.container.servlet.ServletContainer.class, "/*");
        servlet.setInitParameter("com.sun.jersey.config.property.packages", "com.jayway.forest");
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
    public void doTest() {
        Response response = given().
        expect().
        contentType(MediaType.TEXT_HTML).
        when().
        	get("/");
        
        assertTrue(response.getBody().asString().contains("<h2>Commands</h2>"));
    }
}
