package com.jayway.jersey.rest.resource;

import com.jayway.jersey.rest.service.AbstractRunner;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 */
public class PathsTest extends AbstractRunner {


    @Test
    public void testDiscoverId() throws IOException {
        String nameId = get("/bank/other/id/", String.class, "text/html");
        String nameName = get("/bank/other/name/", String.class, "text/html");

        Assert.assertEquals( nameId, nameName );
    }

   @Test
    public void invokeIdResourceAsQuery() {
       try {
           get( "/bank/other/id", String.class );
           Assert.fail();
       } catch (IOException e) {
           Assert.assertTrue( e instanceof FileNotFoundException );
       }
    }

    @Test
    public void invokeIdResourceAsQuery2() {
        try {
            get( "/bank/other/idid", String.class );
            Assert.fail();
        } catch (IOException e) {
            Assert.assertTrue(e instanceof FileNotFoundException);
        }
    }


    @Test
    public void invokeResourceAsQuery() {
        try {
            get( "/bank/other", String.class );
            Assert.fail();
        } catch (IOException e) {
            Assert.assertTrue(e instanceof FileNotFoundException);
        }
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
