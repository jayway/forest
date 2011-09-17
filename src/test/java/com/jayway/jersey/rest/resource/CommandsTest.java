package com.jayway.jersey.rest.resource;

import com.jayway.jersey.rest.dto.StringDTO;
import com.jayway.jersey.rest.service.StateHolder;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.jayway.jersey.rest.service.AbstractRunner;
import junit.framework.Assert;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 */
public class CommandsTest extends AbstractRunner {

    public CommandsTest() throws Exception {
        super( );
    }

    @Test
    public void testEchoMethod() {
        webResource.path("test/command").type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post("\"second\"");

        Assert.assertEquals( "second", StateHolder.get());
    }

    @Test
    public void wrongMethod() {
        try {
            webResource.path("test/command").type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).get( Object.class );
        } catch ( UniformInterfaceException e) {
            Assert.assertEquals( 405 ,e.getResponse().getStatus());
            System.out.println(e.getResponse().getEntity(String.class));
        }
    }


    @Test
    public void testAddCommand() {
        webResource.path("test/addcommand").
                type(MediaType.APPLICATION_JSON).
                accept(MediaType.APPLICATION_JSON).
                post("[10, { \"integer\": \"32\"}]");

        Assert.assertEquals( 42, StateHolder.get());
    }

}
