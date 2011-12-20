package com.jayway.forest.samples.bank.simple.resources;

import javax.ws.rs.Path;

import com.jayway.forest.constraint.Doc;
import com.jayway.forest.roles.Resource;
import com.jayway.forest.samples.bank.simple.resources.accounts.AccountsResource;
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
	
	public int getValue() {
		System.out.println(this + ": " + value);
		return value;
	}
}
