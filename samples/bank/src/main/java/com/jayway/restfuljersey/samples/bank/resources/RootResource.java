package com.jayway.restfuljersey.samples.bank.resources;

import com.jayway.jersey.rest.resource.Resource;
import com.jayway.restfuljersey.samples.bank.resources.accounts.AccountsResource;

public class RootResource extends Resource  {

    public AccountsResource accounts() {
        return new AccountsResource();
    }
}
