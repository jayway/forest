package com.jayway.forest.core;

import com.jayway.forest.service.AbstractRunner;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

import static com.jayway.restassured.RestAssured.given;

public class ExceptionsTest extends AbstractRunner {

    @Test
    public void mappedCheckedExceptionQuery() {
        given().expect().statusCode(409).get("/exceptions/mappedchecked");
    }

    @Test
    public void unmappedCheckedExceptionQuery() {
        given().expect().statusCode(409).get("/exceptions/mappedunchecket");
    }

    @Test
    public void mappedUncheckedExceptionQuery() {
        given().expect().statusCode(500).get("/exceptions/unmappedchecket");
    }

    @Test
    public void unMappedUncheckedExceptionQuery() {
        given().expect().statusCode(500).get("/exceptions/unmappedunchecket");
    }

    @Test
    public void mappedCheckedExceptionCommand() {
        given().expect().statusCode(409).put("/exceptions/mappedcheckedcommand");
    }

    @Test
    public void unmappedCheckedExceptionCommand() {
        given().expect().statusCode(409).put("/exceptions/mappedunchecketcommand");
    }

    @Test
    public void mappedUncheckedExceptionCommand() {
        given().expect().statusCode(500).put("/exceptions/unmappedchecketcommand");
    }

    @Test
    public void unMappedUncheckedExceptionCommand() {
        given().expect().statusCode(500).put("/exceptions/unmappedunchecketcommand");
    }



}

