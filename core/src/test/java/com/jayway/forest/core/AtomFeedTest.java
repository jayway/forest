package com.jayway.forest.core;

import com.jayway.forest.service.AbstractRunner;
import groovyx.net.http.ContentType;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static com.jayway.restassured.RestAssured.given;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.is;
import static groovyx.net.http.ContentType.*;


public class AtomFeedTest extends AbstractRunner {

    @Test
    public void testTemplate() {
        String s = given().expect().statusCode(200).when().get("/listresponse/listhowlong?argument1=7&pageSize=5&format=atom").asString();

        System.out.println(s);
    }


    @Test
    public void testTemplateLinkable() {
        String s = given().expect().statusCode(200).when().get("/listresponse/linkables?format=atom&pageSize=5").asString();

        System.out.printf(s);
    }

}

