package com.jayway.restfuljersey.samples.bank.spring.resources;

import java.util.List;

import com.jayway.restfuljersey.samples.bank.dto.AccountTransformer;
import org.springframework.beans.factory.annotation.Autowired;

import com.jayway.forest.roles.IdDiscoverableResource;
import com.jayway.forest.roles.Linkable;
import com.jayway.forest.roles.Resource;
import com.jayway.forest.servlet.ResponseHandler;
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
        return ResponseHandler.transform( accountRepository.all(), AccountTransformer.INSTANCE );
    }
}
