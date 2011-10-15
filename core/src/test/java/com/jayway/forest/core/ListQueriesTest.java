package com.jayway.forest.core;

import com.jayway.forest.reflection.impl.PagedSortedListResponse;
import com.jayway.forest.service.AbstractRunner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static com.jayway.restassured.RestAssured.get;

public class ListQueriesTest extends AbstractRunner {

    @Test
    public void testListBasic() {
        JSONArray list = getList("/list");
        Assert.assertEquals("Size must be 2", 2, list.size());
        Assert.assertEquals("world", list.get(0));
        Assert.assertEquals("hello", list.get(1));
    }

    @Test
    public void testListComplex() {
        JSONArray list = getList("/liststringdto");
        Assert.assertEquals("Size must be 2", 2, list.size());
        Assert.assertEquals("{\"string\":\"world\"}", list.get(0).toString());
        Assert.assertEquals("{\"string\":\"hello\"}", list.get(1).toString());
    }

    @Test
    public void testListComplexSort() {
        JSONArray list = getList("/liststringdto?sortBy=string");
        Assert.assertEquals("Size must be 2", 2, list.size());
        Assert.assertEquals("{\"string\":\"hello\"}", list.get(0).toString());
        Assert.assertEquals("{\"string\":\"world\"}", list.get(1).toString());
    }

    @Test
    public void testListComplexPaging() {
        PagedSortedListResponse response = get("/liststringdto?pageSize=1&page=2").as(PagedSortedListResponse.class);
        Assert.assertEquals( "Must have two total elements", 2, response.getTotalElements().intValue() );
        Assert.assertEquals("liststringdto?page=1&pageSize=1", response.getPrevious() );
        Assert.assertEquals( "Must have two pages", 2, response.getTotalPages().intValue() );
    }

    @Test
    public void testListPages() {
        PagedSortedListResponse response = get("/listhowlong?argument1=7&pageSize=5").as(PagedSortedListResponse.class);

        Assert.assertEquals("asdf", 2, response.getTotalPages().intValue());
    }

    // cannot get the contained list to be correctly typed
    // unless manually parsing the string representation
    private JSONArray getList(String url) {
        String returned = get(url).andReturn().body().asString();

        JSONObject jsonObject = (JSONObject) JSONValue.parse(returned);
        return (JSONArray) jsonObject.get("list");
    }
}

