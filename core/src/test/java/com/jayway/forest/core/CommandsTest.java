package com.jayway.forest.core;

import com.jayway.forest.service.AbstractRunner;
import com.jayway.forest.service.StateHolder;

import junit.framework.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 */
public class CommandsTest extends AbstractRunner {


    @Test
    public void testEchoMethod() throws IOException {
        post( "/bank/command", "\"second\"" );
        Assert.assertEquals( "second", StateHolder.get());
    }

    @Test
    public void wrongMethod() {
        try {
            get( "/bank/command", Object.class);
            Assert.fail();
        } catch ( IOException e) {
            Assert.assertTrue( e.getMessage().contains("code: 405"));
        }
    }


    @Test
    public void testAddCommand() throws IOException {
        post( "/bank/addcommand", "[10, { \"integer\": 32}]" );
        Assert.assertEquals( 42, StateHolder.get());
    }

    @Test
    public void testCommandList() throws IOException {
        post( "/bank/commandlist", "[\"Hello\"]" );
        Assert.assertEquals( "SuccessHello", StateHolder.get());
    }

    @Test
    public void testIllegalList() throws IOException {
        try {
            post( "/bank/commandlist", "[[\"Hello\"], \"World\"]" );
            Assert.fail( "Must fail!");
        } catch (IOException e ) {
            Assert.assertTrue(e.getMessage().contains("code: 400"));
        }
    }

    @Test
    public void testCommandAddToList() throws IOException {
        post( "/bank/addtolist", "[[\"Hello\"], \"World\"]" );

        List<String> list = (List<String>) StateHolder.get();
        Assert.assertEquals("HelloWorld", list.get(0) + list.get(1));
    }

    @Test
    public void testComplex() throws IOException {
        post( "/bank/complex", "[[[\"Hello\", \"World\"]]]" );

        List<List<List<String>>> list = (List<List<List<String>>>) StateHolder.get();
        String result = "";
        for ( String elm : list.get(0).get(0) ) {
            result += elm;
        }
        Assert.assertEquals( "HelloWorldNEW", result );

    }

}
