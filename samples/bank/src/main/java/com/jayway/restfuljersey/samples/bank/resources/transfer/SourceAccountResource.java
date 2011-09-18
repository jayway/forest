package com.jayway.restfuljersey.samples.bank.resources.transfer;

import com.jayway.jersey.rest.resource.IdResource;
import com.jayway.jersey.rest.resource.IndexResource;
import com.jayway.jersey.rest.resource.Resource;
import com.jayway.restfuljersey.samples.bank.helper.HtmlHelper;
import com.jayway.restfuljersey.samples.bank.model.Account;
import com.jayway.restfuljersey.samples.bank.model.Depositable;
import com.jayway.restfuljersey.samples.bank.model.Withdrawable;
import com.jayway.restfuljersey.samples.bank.repository.AccountRepository;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.Collection;

/**
 */
public class SourceAccountResource extends Resource implements IdResource, IndexResource {

    @Override
    public Resource id(String id) {
        Account source = role(AccountRepository.class).findById(id);
        if ( allowed().contains( source ) ) {
            addRole( Depositable.class, source );
            return new DestinationAccountResource();
        }
        throw new WebApplicationException(Response.Status.NOT_FOUND );
    }

    @Override
    public Object index() {
        return HtmlHelper.toLinks(allowed());
    }

    private Collection<Account> allowed() {
        Collection<Account> accounts = role(AccountRepository.class).withRole(Depositable.class);
        accounts.remove( role(Withdrawable.class) );
        return accounts;
    }

}
