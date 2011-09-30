package com.jayway.restfuljersey.samples.bank.spring.resources;

import static com.jayway.forest.grove.RoleManager.role;

import java.util.List;

import com.jayway.jersey.rest.resource.Resource;
import com.jayway.jersey.rest.resource.ResponseHandler;
import com.jayway.jersey.rest.roles.IdDiscoverableResource;
import com.jayway.jersey.rest.roles.Linkable;
import com.jayway.restfuljersey.samples.bank.model.Account;
import com.jayway.restfuljersey.samples.bank.repository.AccountRepository;

public class AccountsResource implements Resource, IdDiscoverableResource {

    @Override
    public Resource id(String id) {
        Account account = role(AccountRepository.class).findById(id);
        return new AccountResource(account);
    }

    @Override
    public List<Linkable> discover() {
        return ResponseHandler.mapList(Account.class, role(AccountRepository.class).all(), "number", "number" );
    }
}
