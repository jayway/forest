package com.jayway.restfuljersey.samples.bank.resources;

import com.jayway.jersey.rest.constraint.Doc;
import com.jayway.jersey.rest.resource.Resource;
import com.jayway.restfuljersey.samples.bank.resources.accounts.AccountsResource;

public class RootResource implements Resource {

    @Doc( "Lists all accounts in the system" )
    public AccountsResource accounts() {
        return new AccountsResource();
    }
}
