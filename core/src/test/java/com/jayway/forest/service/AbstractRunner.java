package com.jayway.forest.service;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.specification.RequestSpecification;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.mortbay.jetty.testing.ServletTester;

import static com.jayway.restassured.parsing.Parser.JSON;

/**
 */
public class AbstractRunner {

    private static ServletTester tester;

    @Before
    public void setup() {
        StateHolder.set(null);
        RestfulServletService.reset();
    }


    @BeforeClass
    public static void initServletContainer () throws Exception
    {
        tester = new ServletTester();
        tester.setContextPath("/");
        tester.addServlet(RestfulServletService.class, "/bank/*");
        RestAssured.baseURI = tester.createSocketConnector(true);
        RestAssured.defaultParser = JSON;
        RestAssured.basePath = "/bank";
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

    protected RequestSpecification contentTypeFormUrlEncoded() {
        return new RequestSpecBuilder().addHeader( "Content-type", "application/x-www-form-urlencoded").addHeader("Accept", "text/html").build();
    }

    protected RequestSpecification acceptTextHtml() {
        return new RequestSpecBuilder().addHeader("Accept", "text/html").build();
    }

    protected RequestSpecification acceptAtomXml() {
        return new RequestSpecBuilder().addHeader("Accept", "application/atom+xml").build();
    }

}
