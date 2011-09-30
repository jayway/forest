package com.jayway.restfuljersey.samples.bank.spring.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.jayway.jersey.rest.resource.Resource;
import com.jayway.jersey.rest.resource.ResponseHandler;
import com.jayway.jersey.rest.roles.IdDiscoverableResource;
import com.jayway.jersey.rest.roles.Linkable;
import com.jayway.restfuljersey.samples.bank.model.Account;
import com.jayway.restfuljersey.samples.bank.repository.AccountRepository;

public class AccountsResource implements Resource, IdDiscoverableResource {
	
	@Autowired
	private AccountRepository accountRepository;

    @Override
    public Resource id(String id) {
        Account account = accountRepository.findById(id);
        return new AccountResource(account);
    }

    @Override
    public List<Linkable> discover() {
        return ResponseHandler.mapList(Account.class, accountRepository.all(), "number", "number" );
    }
}
