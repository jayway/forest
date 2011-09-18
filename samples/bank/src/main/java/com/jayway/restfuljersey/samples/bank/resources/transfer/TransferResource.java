package com.jayway.restfuljersey.samples.bank.resources.transfer;

import com.jayway.jersey.rest.resource.IdResource;
import com.jayway.jersey.rest.resource.IndexResource;
import com.jayway.jersey.rest.resource.Resource;
import com.jayway.restfuljersey.samples.bank.helper.HtmlHelper;
import com.jayway.restfuljersey.samples.bank.model.Account;
import com.jayway.restfuljersey.samples.bank.model.Withdrawable;
import com.jayway.restfuljersey.samples.bank.repository.AccountRepository;

/**
 */
public class TransferResource extends Resource implements IdResource, IndexResource {

    @Override
    public Resource id( String id ) {
        AccountRepository accountRepository = role(AccountRepository.class);
        Account account = accountRepository.findWithRole(id, Withdrawable.class);
        addRole( Withdrawable.class, account );
        return new SourceAccountResource();
    }

    @Override
    public Object index() {
        return HtmlHelper.toLinks(role(AccountRepository.class).withRole(Withdrawable.class));
    }
}
