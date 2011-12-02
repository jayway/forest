package com.jayway.forest.service;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import com.jayway.forest.dto.IntegerDTO;
import com.jayway.forest.roles.Resource;

@Path("bytecode")
public class ByteCodeResource implements Resource {
    public String noArgQuery() {
    	return "Hello world";
    }

    @Path("overriddenPath")
    public String normalPath() {
    	return "Hello world";
    }

    @POST
    public String doPost() {
    	return "Hello world";
    }
    
    private static int count = 0;

    public void incCommand() {
    	count++;
    }

    public void add(String i) {
    	count += Integer.parseInt(i);
    }

    public void adddto(IntegerDTO i) {
    	count += i.getInteger();
    }

    public void addMultiple(String i, String j) {
    	count += Integer.parseInt(i)*Integer.parseInt(j);
    }

    public String getcount() {
    	return "" + count;
    }

    public String echo(String text) {
    	return text;
    }

	public static void reset() {
		count = 0;
	}
}

