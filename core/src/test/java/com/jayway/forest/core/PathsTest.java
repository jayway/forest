package com.jayway.forest.core;

import com.jayway.forest.service.AbstractRunner;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.expect;

/**
 */
public class PathsTest extends AbstractRunner {

   @Test
    public void invokeIdResourceAsQuery() {
       expect().statusCode(404).when().get("/other/id");
    }

    @Test
    public void invokeIdResourceAsQuery2() {
        expect().statusCode(404).when().get( "/other/idid");
    }

    @Test
    public void invokeResourceAsQuery() {
        expect().statusCode(404).when().get( "/other");
    }

    @Test
    public void invokeIdResourceAsCommand() {
        expect().statusCode( 404).when().post("/other/id");
        expect().statusCode( 404).when().put("/other/id");
        expect().statusCode( 404).when().delete("/other/id");
    }

    @Test
    public void invokeResourceAsCommand() {
        expect().statusCode( 404).when().post("/other");
        expect().statusCode( 404).when().put("/other");
        expect().statusCode( 404).when().delete("/other");
    }


    @Test
    public void onPath() {
        expect().statusCode( 405 ).when().put("/other/id/");
        expect().statusCode( 404 ).when().post("/other/id/");
        expect().statusCode( 404 ).when().delete("/other/id/");
    }

    @Test
    public void nullPath() {
        tester.setContextPath( "" );
        expect().statusCode( 404).when().get("");
    }

}
