package com.jayway.forest.core;

import com.jayway.forest.dto.IntegerDTO;
import com.jayway.forest.dto.StringDTO;
import com.jayway.forest.service.AbstractRunner;
import com.jayway.forest.service.StateHolder;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class QueriesTest  extends AbstractRunner {

    @Test
    public void testInvokeIndex() throws IOException {
        StateHolder.set("Expected String");

        StringDTO returned = get("/bank/other/description", StringDTO.class);
        Assert.assertEquals( "Expected String", returned.string() );
    }

    @Test
    public void testQueryWithInteger() throws IOException {
        queryParam("argument1", "60");
        IntegerDTO integer = get( "/bank/addten", IntegerDTO.class );

        Assert.assertEquals(70, integer.getInteger().intValue());
    }

    @Test
    public void testQueryWithIntegerWrongInput() throws IOException {

        queryParam( "argument1", "x6f?0");
        try {
            get( "/bank/addten", IntegerDTO.class );
            Assert.fail();
        } catch (IOException e ) {
            Assert.assertTrue( e.getMessage().contains( "code: 400"));
        }
    }


    @Test
    public void testQueryWithIntegerAndInteger() throws IOException {
        queryParam( "argument1", "60");
        queryParam( "argument2.IntegerDTO.integer", "13");
        Integer aLong = get("/bank/add", Integer.class);
        Assert.assertEquals(73, aLong.intValue());
    }

}
