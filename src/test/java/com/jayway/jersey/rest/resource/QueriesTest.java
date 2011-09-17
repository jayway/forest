package com.jayway.jersey.rest.resource;

import com.jayway.jersey.rest.dto.IntegerDTO;
import com.jayway.jersey.rest.dto.StringDTO;
import com.jayway.jersey.rest.service.AbstractRunner;
import com.jayway.jersey.rest.service.StateHolder;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.MediaType;

/**
 */
public class QueriesTest extends AbstractRunner {

    public QueriesTest() throws Exception {
        super( );
    }

     @Test
     public void testEchoMethod() {
         String response = webResource.path("test/echo").queryParam("argument1", "echo")
                 .type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).get(String.class);

         Assert.assertEquals( "echo", response );
     }


    @Test
    public void testEchoMethodXml() {
        String response = webResource.path("test/echo").queryParam("argument1", "echo")
                .type(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML).get(String.class);

        Assert.assertEquals( "echo", response );
    }

    @Test
    public void testInvokeIndex() {
        String expected = "Expected String";
        StateHolder.set( expected );
        webResource.path("test/other/index").accept( MediaType.APPLICATION_JSON ).get( StringDTO.class );
        StringDTO result = (StringDTO) StateHolder.get();
        Assert.assertEquals( expected, result.string() );
    }


    @Test
    public void testQueryWithInteger() {
        IntegerDTO integer = webResource.path("test/addten").queryParam("argument1", "60")
                .type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).get(IntegerDTO.class);
        Assert.assertEquals( 70, integer.getInteger().intValue() );
    }

    @Test
    public void testQueryWithIntegerWrongInput() {
        mustThrow(webResource.path("test/addten").queryParam("argument1", "x6f?0")
                .accept(MediaType.APPLICATION_JSON), "GET", IntegerDTO.class, 400);
    }


    @Test
    public void testQueryWithIntegerAndInteger() {
        IntegerDTO integer = webResource.path("test/add").queryParam("argument1", "60").queryParam( "argument2.IntegerDTO.integer", "13" )
                .type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).get(IntegerDTO.class);
        Assert.assertEquals( 73, integer.getInteger().intValue() );
    }

}
