package com.jayway.forest.core;

import com.jayway.forest.dto.StringAndIntegerDTO;
import com.jayway.forest.service.AbstractRunner;
import com.jayway.forest.service.StateHolder;
import org.junit.Assert;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.given;

/**
 */
public class UpdatableTest extends AbstractRunner {

    @Test
    public void testUpdate() {
        given().body("{\"string\":\"StringValue\", \"integer\": 60}").
        expect().statusCode( 200 )
            .when().put("/update/");

        StringAndIntegerDTO actual = (StringAndIntegerDTO) StateHolder.get();
        Assert.assertEquals( "StringValue", actual.getString() );
        Assert.assertEquals( 60, actual.getInteger().intValue() );
    }

    @Test
    public void testUpdateHtml() {
        given().spec( contentTypeFormUrlEncoded() ).expect().statusCode(200)
                .post("/update/update?argument1.StringAndIntegerDTO.string=hello&argument1.StringAndIntegerDTO.integer=62" );

        StringAndIntegerDTO actual = (StringAndIntegerDTO) StateHolder.get();
        Assert.assertEquals( "hello", actual.getString() );
        Assert.assertEquals(62, actual.getInteger().intValue());
    }
}
