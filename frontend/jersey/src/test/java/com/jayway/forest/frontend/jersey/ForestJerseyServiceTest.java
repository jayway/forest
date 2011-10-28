package com.jayway.forest.frontend.jersey;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.sun.jersey.test.framework.JerseyTest;

public class ForestJerseyServiceTest extends JerseyTest {
	
	public ForestJerseyServiceTest() throws Exception {
        super( "com.jayway.forest.frontend.jersey.test" );
	}

    @Test
    public void expectAddToWork() throws IOException {
    	String string = webResource.path("/add").queryParam("argument1", "60").queryParam("argument2.IntegerDTO.integer", "13").get(String.class);
    	assertEquals("73", string);
    }
}
//static {
//	System.out.println("------" + GrizzlyWebServer.class.getProtectionDomain().getCodeSource().getLocation());
//}
// ------file:/Users/jan/.m2/repository/org/glassfish/embedded/glassfish-embedded-all/3.0-Prelude-Embedded-b10/glassfish-embedded-all-3.0-Prelude-Embedded-b10.jar
// ------file:/Users/jan/.m2/repository/com/sun/grizzly/grizzly-servlet-webserver/1.9.8/grizzly-servlet-webserver-1.9.8.jar
