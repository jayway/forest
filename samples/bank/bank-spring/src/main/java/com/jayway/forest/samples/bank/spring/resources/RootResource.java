package com.jayway.forest.samples.bank.spring.resources;

import com.jayway.forest.legacy.constraint.Doc;
import com.jayway.forest.legacy.roles.Resource;

public class RootResource implements Resource {

    @Doc( "Lists all accounts in the system" )
    public AccountsResource accounts() {
        return new AccountsResource();
    }
}
