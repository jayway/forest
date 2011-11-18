package com.jayway.forest.samples.bank.jersey.resources;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.parsing.Parser.JSON;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.testing.ServletTester;

import com.jayway.restassured.RestAssured;

public class JavaassistTest {
    private static ServletTester tester;

	@BeforeClass
    public static void initServletContainer () throws Exception
    {
        tester = new ServletTester();
        tester.setContextPath("/app");
        ServletHolder servlet = tester.addServlet(com.sun.jersey.spi.container.servlet.ServletContainer.class, "/*");
//        servlet.setInitParameter("com.sun.jersey.config.property.packages", "com.jayway.restfuljersey.samples.bank.jersey.resources");
        servlet.setInitParameter("javax.ws.rs.Application", "com.jayway.forest.samples.bank.jersey.resources.MyApplication");
        servlet.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature", "true");
        servlet.setInitParameter("com.sun.jersey.spi.container.ResourceFilters", "com.jayway.forest.samples.bank.jersey.MyResourceFilterFactory");
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
    public void doTestByteCode() {
        given().
        expect().
        	body(is("qwe")).
        when().
        	get("/simplePrint");
    }

    @Test
    public void doTest() {
        given().
        	queryParam("param", "hello").
        expect().
        	body(is("hello")).
        when().
        	get("/simpleEcho");
    }

    @Test
    public void doTest2() {
        given().
        expect().
        	body(containsString("Balance = 100")).
        when().
        	get("/accounts/11111/description");

        given().
        	formParam("amount", 50).
        expect().
        	statusCode(204).
        when().
        	put("/accounts/11111/deposit");

        given().
        expect().
        	body(containsString("Balance = 150")).
        when().
        	get("/accounts/11111/description");
    }

    @Test
    @Ignore
    public void testConstraints() {
        given().
        	formParam("amount", 50).
        expect().
        	statusCode(204).
        when().
        	put("/accounts/11111/withdraw");
    }
}
