package com.jayway.forest.legacy.core;

import com.jayway.forest.legacy.service.AbstractRunner;
import com.jayway.forest.legacy.service.StateHolder;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 */
public class CommandsTest extends AbstractRunner {

    @Test
    public void testEchoMethod() throws IOException {
        given().
                body("\"second\"").
        expect().
                statusCode(200).
                body(containsString("Operation completed successfully")).
        when().
                post("/command");

        assertThat(StateHolder.get().toString(), equalTo("second"));
    }

    @Test
    public void wrongMethod() {
        expect().statusCode(405).when().get( "/command");
    }

    @Test
    public void testAddCommand() {
        given().body("[10, \n{ \"integer\": 32}]").when().post("/addcommand");
        assertEquals(42, StateHolder.get());
    }

    @Test
    public void testCommandList() throws IOException {
        given().body("[\"Hello\"]").when().post("/commandlist");

        assertEquals("SuccessHello", StateHolder.get());
    }

    @Test
    public void testIllegalList() {
            given().
                    body("[[\"Hello\"], \"World\"]").
            expect().
                    statusCode(400).
            when().
                    post( "/commandlist");
    }

    @Test @SuppressWarnings("unchecked")
    public void testCommandAddToList() {
        given().body("[[\"Hello\"], \"World\"]").expect().statusCode(200).when().post("/addtolist");

		List<String> list = (List<String>) StateHolder.get();
        assertEquals("HelloWorld", list.get(0) + list.get(1));
    }

    @Test @SuppressWarnings("unchecked")
    public void testComplex() {
        given().body("[[[\"Hello\", \"World\"]]]").expect().statusCode(200).when().post("/complex");

		List<List<List<String>>> list = (List<List<List<String>>>) StateHolder.get();
        String result = "";
        for ( String elm : list.get(0).get(0) ) {
            result += elm;
        }
        assertEquals("HelloWorldNEW", result);
    }

    @Test
    public void testDeleteHTML() {
        given().spec( contentTypeFormUrlEncoded() ).expect().statusCode(200).when().post("/other/delete");
        String message = (String) StateHolder.get();
        System.out.printf(message );
    }

}
