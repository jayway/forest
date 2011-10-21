package com.jayway.forest.core;

import com.jayway.forest.service.AbstractRunner;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

import static com.jayway.restassured.RestAssured.given;

public class ExceptionsTest extends AbstractRunner {

    @Test
    public void mappedCheckedExceptionQuery() {
        given().expect().statusCode(409).get("/mappedchecked");
    }

    @Test
    public void unmappedCheckedExceptionQuery() {
        given().expect().statusCode(409).get("/mappedunchecket");
    }

    @Test
    public void mappedUncheckedExceptionQuery() {
        given().expect().statusCode(500).get("/unmappedchecket");
    }

    @Test
    public void unMappedUncheckedExceptionQuery() {
        given().expect().statusCode(500).get("/unmappedunchecket");
    }


    
    @Test
    public void mappedCheckedExceptionCommand() {
        given().expect().statusCode(409).post("/mappedcheckedcommand");
    }

    @Test
    public void unmappedCheckedExceptionCommand() {
        given().expect().statusCode(409).post("/mappedunchecketcommand");
    }

    @Test
    public void mappedUncheckedExceptionCommand() {
        given().expect().statusCode(500).post("/unmappedchecketcommand");
    }

    @Test
    public void unMappedUncheckedExceptionCommand() {
        given().expect().statusCode(500).post("/unmappedunchecketcommand");
    }



}

