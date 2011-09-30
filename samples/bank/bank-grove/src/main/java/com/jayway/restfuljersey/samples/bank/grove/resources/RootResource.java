package com.jayway.restfuljersey.samples.bank.grove.resources;

import com.jayway.forest.constraint.Doc;
import com.jayway.forest.roles.Resource;
import com.jayway.restfuljersey.samples.bank.grove.resources.accounts.AccountsResource;

public class RootResource implements Resource {

    @Doc( "Lists all accounts in the system" )
    public AccountsResource accounts() {
        return new AccountsResource();
    }
}
