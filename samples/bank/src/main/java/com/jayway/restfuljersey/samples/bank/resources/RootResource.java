package com.jayway.restfuljersey.samples.bank.resources;

import com.jayway.jersey.rest.constraint.Doc;
import com.jayway.jersey.rest.resource.Resource;
import com.jayway.restfuljersey.samples.bank.dto.CrazyDTO;
import com.jayway.restfuljersey.samples.bank.resources.accounts.AccountsResource;

import java.lang.annotation.Documented;

public class RootResource extends Resource {

    @Doc( "Lists all accounts in the system" )
    public AccountsResource accounts() {
        return new AccountsResource();
    }
}
