package com.jayway.forest.service;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

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

    public void add(@FormParam("argument1") String i) {
    	count += Integer.parseInt(i);
    }

    public String getcount() {
    	return "" + count;
    }

	public static void reset() {
		count = 0;
	}
}

