package com.jayway.restfuljersey.samples.bank.spring.resources;

import com.jayway.jersey.rest.constraint.Doc;
import com.jayway.jersey.rest.resource.Resource;

public class RootResource implements Resource {

    @Doc( "Lists all accounts in the system" )
    public AccountsResource accounts() {
        return new AccountsResource();
    }
}
