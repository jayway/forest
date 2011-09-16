package com.jayway.jersey.rest.resource;

import com.jayway.jersey.rest.service.AbstractRunner;
import com.jayway.jersey.rest.service.StateHolder;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.MediaType;

/**
 */
public class PathsTest extends AbstractRunner {


    public PathsTest() throws Exception {
        super();
    }

    @Test
    public void testDiscoverId() {
        String nameId = webResource.path("test/other/id/").type(MediaType.TEXT_HTML).get(String.class);
        String nameName = webResource.path("test/other/name/").type(MediaType.TEXT_HTML).get(String.class);

        Assert.assertEquals( nameId, nameName );
    }

    @Test
    public void invokeIdResourceAsQuery() {
        mustThrow(webResource.path("test/other/id").type(MediaType.TEXT_HTML), "GET", String.class, 404);
    }

    @Test
    public void invokeIdResourceAsQuery2() {
        mustThrow(webResource.path("test/other/idid").type(MediaType.TEXT_HTML), "GET", String.class, 404);
    }


    @Test
    public void invokeResourceAsQuery() {
        mustThrow( webResource.path("test/other").type(MediaType.TEXT_HTML), "GET", String.class, 404);
    }

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

}
