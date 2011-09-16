package com.jayway.jersey.rest.resource;

import com.jayway.jersey.rest.service.AbstractRunner;
import com.jayway.jersey.rest.service.StateHolder;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.servlet.ServletOutputStream;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

/**
 */
public class ConstraintsTest extends AbstractRunner {

    public ConstraintsTest() throws Exception {
        super( );
    }

    @Test
    public void testConstraint() {
        StateHolder.set("Hello World");

        String result = webResource.path("test/constraint").type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).get( String.class );

        Assert.assertEquals( "Hello World", result );
    }

    @Test
    public void testIlllegalConstraint() {
        mustThrow(webResource.path("test/constraint").accept(MediaType.APPLICATION_JSON), "GET", String.class, 404);

    }


    @Test
    public void testIllegalDelete() {
        mustThrow(webResource.path("test/other/constraint/").accept(MediaType.APPLICATION_JSON), "DELETE", null, 404);
    }

    /**
     * This tests that the discovery of  
     */
    @Test
    public void testDiscocerNoIndex() throws IOException {
        HtmlHelper mock = Mockito.mock(HtmlHelper.class);

        Mockito.doAnswer( new Answer<Object>() {
            public Object answer(InvocationOnMock
                                         invocation) {
                StateHolder.set(invocation.getArguments()[1]);
                return "invoked";
            }
        }).when( mock ).addResourceMethods( Mockito.any( ServletOutputStream.class), Mockito.anyList() );

        StateHolder.set(mock);

        webResource.path("test/other/constraint/").type(MediaType.TEXT_HTML).get( String.class );
    }
}
