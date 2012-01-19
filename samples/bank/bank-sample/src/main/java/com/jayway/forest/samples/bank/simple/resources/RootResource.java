package com.jayway.forest.samples.bank.simple.resources;

import javax.ws.rs.Path;

import com.jayway.forest.roles.Resource;
import com.jayway.forest.roles.Template;
import com.sun.jersey.spi.resource.Singleton;

@Path("")
@Singleton
public class RootResource implements Resource {

//    @Doc( "Lists all accounts in the system" )
//    public AccountsResource accounts() {
//        return new AccountsResource();
//    }
	
	private int value = 0;
	
	public void inc() {
		value++;
		System.out.println(this + ": " + value);
	}

	public void add(int adder, @Template("defaultMessage") String message) {
		value += adder;
		System.out.println(this + ": " + value + " message=" + message);
	}
	
	String defaultMessage() {
		return "DEFAULT!!";
	}

	public int getValue() {
		System.out.println(this + ": " + value);
		return value;
	}
}
