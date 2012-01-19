package com.jayway.forest.core;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.jayway.forest.dto.IntegerDTO;
import com.jayway.forest.service.AbstractRunner;
import com.jayway.forest.service.StateHolder;

@Ignore
public class CommandsTest extends AbstractRunner {

    @Test
    public void testEchoMethod() throws IOException {
        given().
                body("second").
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
    @Ignore("@FormParam has certain requirements for unmarshalling that is not fulfilled by IntegerDTO")
    public void testAddCommand() {
        given().
//        	body("[10, \n{ \"integer\": 32}]").
        	formParam("argument1", 10).
        	formParam("argument2", new IntegerDTO(32)).
        expect().
        	statusCode(200).
        when().
        	put("/addcommand");
        assertEquals(42, StateHolder.get());
    }

    @Test
    public void testAddCommandPrimitive() {
        given().
//        	body("[10, \n{ \"integer\": 32}]").
        	formParam("argument1", 10).
        	formParam("argument2", 32).
        expect().
        	statusCode(200).
        when().
        	put("/addcommandprimitive");
        assertEquals(42, StateHolder.get());
    }

    @Test
    public void testCommandList() throws IOException {
        given().body("[\"Hello\"]").when().post("/commandlist");

        assertEquals("SuccessHello", StateHolder.get());
    }

    @Test
    @Ignore("We get a 500 due to classcast exception instead of 400")
    public void testIllegalList() {
            given().
                    body("[[\"Hello\"], \"World\"]").
            expect().
                    statusCode(400).
            when().
                    post( "/commandlist");
    }

    @Test @SuppressWarnings("unchecked")
    @Ignore("Don't know how to represent the list")
    public void testCommandAddToList() {
        given().
//    		formParam("argument1", Arrays.asList("Hello", "Cruel")).
    		formParam("argument1", "Hello,Cruel").
        	formParam("argument2", "World")
        .expect().statusCode(200).when().put("/addtolist");

		List<String> list = (List<String>) StateHolder.get();
        assertEquals(3, list.size());
        assertEquals("HelloCruelWorld", list.get(0) + list.get(1));
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
