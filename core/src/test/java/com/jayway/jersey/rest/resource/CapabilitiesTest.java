package com.jayway.jersey.rest.resource;

import com.jayway.jersey.rest.service.AbstractRunner;
import com.jayway.jersey.rest.service.StateHolder;
import com.sun.jersey.api.client.UniformInterfaceException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.servlet.ServletOutputStream;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;

/**
 */
public class CapabilitiesTest extends AbstractRunner {
    
    public CapabilitiesTest() throws Exception {
        super();
    }

    @Test
    public void testRoot() {
        String response = webResource.path("test/").accept(MediaType.TEXT_HTML).get(String.class);
        System.out.println( response );
    }

    @Test
    public void testPathEvaluation() {
        String root = webResource.path("test/").type(MediaType.TEXT_HTML).get(String.class);
        String subsub = webResource.path("test/sub/sub/").type(MediaType.TEXT_HTML).get(String.class);
        Assert.assertEquals( root, subsub );
    }

    @Test
    public void discover() throws IOException {
        HtmlHelper mock = Mockito.mock(HtmlHelper.class);

        Mockito.doAnswer( new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) {
                StateHolder.set( invocation.getArguments()[1] );
                return "invoked";
            }
        }).when( mock ).addResourceMethods( Mockito.any( ServletOutputStream.class), Mockito.anyList() );

        StateHolder.set( mock );

        webResource.path("test/").type(MediaType.TEXT_HTML).get(String.class);

        ArrayList<Resource.ResourceMethod> list = (ArrayList<Resource.ResourceMethod>) StateHolder.get();

        StringBuilder sb = new StringBuilder();
        for ( Resource.ResourceMethod method : list ) {
            sb.append( method.name() ).append( ":" ).append( method.type() ).append(",");
        }
        String result = sb.toString();
        hasMethod(result, "command:COMMAND");
        hasMethod(result, "sub:SUBRESOURCE");
        hasMethod(result, "other:SUBRESOURCE" );
        hasMethod(result, "addten:QUERY");
        hasMethod(result, "echo:QUERY");
        hasMethod(result, "add:QUERY");
        hasMethod(result, "addcommand:COMMAND");

        Assert.assertEquals( "Must have 8 ResourceMethods", 8, list.size() );
    }

    private void hasMethod( String result, String name ) {
        Assert.assertTrue("Must contain "+name , result.contains( name ));
    }

    @Test
    public void testPathEvaluationWrong() {
        try {
            webResource.path("test/sub/sub2/").type(MediaType.TEXT_HTML).get(String.class);
            Assert.fail( "must throw Not Found" );
        } catch( UniformInterfaceException e) {
            Assert.assertEquals( 404, e.getResponse().getStatus() );
        }

    }

    @Test
    public void testUnsupportedMediaType() {
        try {
            webResource.path("test/").type( MediaType.APPLICATION_OCTET_STREAM ).get( String.class );
            Assert.fail( "Must throw unsupported media type" );
        } catch( UniformInterfaceException e) {
            Assert.assertEquals( 415, e.getResponse().getStatus() );
        }
    }

    @Test
    public void testMediaTypes() {
        webResource.path("test/sub/").type( MediaType.APPLICATION_XML ).get( String.class );
        webResource.path("test/sub/").type( MediaType.APPLICATION_JSON ).get( String.class );
        webResource.path("test/sub/").type( MediaType.TEXT_HTML ).get( String.class );
    }


}
