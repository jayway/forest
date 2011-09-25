package com.jayway.restfuljersey.samples.bank.resources.accounts;

import com.jayway.jersey.rest.resource.ResponseHandler;
import com.jayway.jersey.rest.roles.IdDiscoverableResource;
import com.jayway.jersey.rest.roles.IdResource;
import com.jayway.jersey.rest.roles.DescribedResource;
import com.jayway.jersey.rest.resource.Resource;
import com.jayway.jersey.rest.roles.Linkable;
import com.jayway.restfuljersey.samples.bank.helper.HtmlHelper;
import com.jayway.restfuljersey.samples.bank.model.Account;
import com.jayway.restfuljersey.samples.bank.repository.AccountRepository;

import java.util.Collection;
import java.util.List;

public class AccountsResource extends Resource implements IdDiscoverableResource {

    @Override
    public Resource id(String id) {
        Account account = context(AccountRepository.class).findById(id);
        addToContext(Account.class, account);
        return new AccountResource();
    }

    @Override
    public List<Linkable> discover() {
        return ResponseHandler.mapList(Account.class, context(AccountRepository.class).all(), "number", "number" );
    }
}
