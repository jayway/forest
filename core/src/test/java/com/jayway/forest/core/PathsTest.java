package com.jayway.forest.core;

import com.jayway.forest.service.AbstractRunner;
import org.junit.Test;

import java.io.IOException;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

/**
 */
public class PathsTest extends AbstractRunner {


    /*@Test
    public void testDiscoverId() throws IOException {
        String nameId = given().and().get("/other/id/").asString();
        String nameName = given().and().get("/other/name/").asString();

        assertEquals(nameId, nameName);
    }*/

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

/*
    @Test
    public void invokeIdResourceAsCommand() {
        mustThrow( webResource.path("test/other/id").type(MediaType.APPLICATION_JSON), "POST", null, 404);
        mustThrow( webResource.path("test/other/id").type(MediaType.APPLICATION_JSON), "PUT", null, 404);
        mustThrow( webResource.path("test/other/id").type(MediaType.APPLICATION_JSON), "DELETE", null, 405);
    }

    @Test
    public void invokeResourceAsCommand() {
        mustThrow( webResource.path("test/other").type(MediaType.APPLICATION_JSON), "POST", null, 404);
        mustThrow( webResource.path("test/other").type(MediaType.APPLICATION_JSON), "PUT", null, 404);
        mustThrow( webResource.path("test/other").type(MediaType.APPLICATION_JSON), "DELETE", null, 405);
    }


    @Test
    public void putOnPath() {
        mustThrow( webResource.path("test/other/id/").type(MediaType.APPLICATION_JSON), "PUT", null, 405);
    }

    @Test
    public void postOnPath() {
        mustThrow( webResource.path("test/other/id/").type(MediaType.APPLICATION_JSON), "POST", null, 405);
    }

    @Test
    public void deleteOnPath() {
        mustThrow( webResource.path("test/").type(MediaType.APPLICATION_JSON), "DELETE", null, 405);
        webResource.path("test/other/").type(MediaType.APPLICATION_JSON).delete();
        Assert.assertEquals( "Delete invoked", StateHolder.get() );
    }

    @Test
    public void deleteAsCommand() {
        webResource.path("test/other/delete").type(MediaType.APPLICATION_JSON).post();
        Assert.assertEquals( "Delete invoked", StateHolder.get() );
    }
*/
}
