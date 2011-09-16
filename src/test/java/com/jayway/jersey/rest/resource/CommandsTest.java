package com.jayway.jersey.rest.resource;

import com.jayway.jersey.rest.service.AbstractRunner;
import com.sun.jersey.api.client.UniformInterfaceException;
import org.junit.Test;

import javax.ws.rs.core.MediaType;

/**
 */
public class CommandsTest extends AbstractRunner {

    public CommandsTest() throws Exception {
        super( );
    }

    @Test
    public void testEchoMethod() {
        webResource.path("test/command").type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post("{\"string\":\"second\"}");
    }

    @Test
    public void wrongMethod() {
        try {
            webResource.path("test/command").type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).get( String.class );
        } catch ( UniformInterfaceException e) {
            System.out.println(e.getResponse().getEntity( String.class) );
        }
    }

}
