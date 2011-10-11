package com.jayway.restfuljersey.samples.bank.grove.resources.accounts;

import com.jayway.forest.constraint.Doc;
import com.jayway.forest.core.RoleManager;
import com.jayway.forest.roles.DescribedResource;
import com.jayway.forest.roles.IdDiscoverableResource;
import com.jayway.forest.roles.Linkable;
import com.jayway.forest.roles.Resource;
import com.jayway.forest.servlet.ResponseHandler;
import com.jayway.restfuljersey.samples.bank.dto.AccountLinkable;
import com.jayway.restfuljersey.samples.bank.dto.AccountTransformer;
import com.jayway.restfuljersey.samples.bank.model.Account;
import com.jayway.restfuljersey.samples.bank.model.CheckingAccount;
import com.jayway.restfuljersey.samples.bank.repository.AccountRepository;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.jayway.forest.core.RoleManager.*;

public class AccountsResource implements IdDiscoverableResource {

    @Doc("Dummy for testing list queries with argument")
    public List<Linkable> search( String name ) {
        List<Linkable> list = new ArrayList<Linkable>();
        list.add( new Linkable( name + "/", name));
        list.add( new Linkable( "other"+name + "/", "other"+name));
        return list;
    }

    @Doc("returning a list of something not Linkable")
    public List<CheckingAccount> overdrawscheckingaccounts() {
        List<CheckingAccount> overdrawn = new ArrayList<CheckingAccount>();
        for (Account account : role(AccountRepository.class).all()) {
            if ( account instanceof CheckingAccount && account.getBalance() < 0 ) overdrawn.add((CheckingAccount) account);
        }
        return overdrawn;
    }

    @Doc("returning a list of AccountLinkable")
    public List<AccountLinkable> overdrawn() {
        List<AccountLinkable> overdrawn = new ArrayList<AccountLinkable>();
        for (Account account : role(AccountRepository.class).all()) {
            if ( account.getBalance() < 0 ) overdrawn.add( new AccountLinkable( account.getAccountNumber(), account.getName(), account.getBalance() ));
        }
        return overdrawn;
    }

    @Override
    public Resource id(String id) {
        Account account = role(AccountRepository.class).findById(id);
        return new AccountResource( account );
    }

    @Doc("returning a list of Linkable")
    @Override
    public List<Linkable> discover() {
        return ResponseHandler.transform( role(AccountRepository.class).all(), AccountTransformer.INSTANCE );
    }

}
