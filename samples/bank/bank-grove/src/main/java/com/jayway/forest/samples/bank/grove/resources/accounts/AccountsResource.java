package com.jayway.forest.samples.bank.grove.resources.accounts;

import com.jayway.forest.constraint.Doc;
import com.jayway.forest.core.JSONHelper;
import com.jayway.forest.exceptions.NotFoundException;
import com.jayway.forest.roles.IdDiscoverableResource;
import com.jayway.forest.roles.Linkable;
import com.jayway.forest.roles.Resource;
import com.jayway.forest.servlet.ResponseHandler;
import com.jayway.forest.samples.bank.dto.AccountLinkable;
import com.jayway.forest.samples.bank.dto.AccountTransformer;
import com.jayway.forest.samples.bank.model.Account;
import com.jayway.forest.samples.bank.model.CheckingAccount;
import com.jayway.forest.samples.bank.repository.AccountRepository;

import java.util.ArrayList;
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
        JSONHelper jsonHelper = new JSONHelper();
        for (Account account : role(AccountRepository.class).all()) {
            if ( account.getBalance() < 0 ) {
                String description = jsonHelper.toJSON(account).toString();
                overdrawn.add( new AccountLinkable( account.getAccountNumber(), account.getName(), description ,account.getBalance() ));
            }
        }
        return overdrawn;
    }

    @Override
    public Resource id(String id) {
        Account account = role(AccountRepository.class).findById(id);
        if ( account == null ) throw new NotFoundException( "No account with id '" +id + "' found" );
        return new AccountResource( account );
    }

    @Doc("returning a list of Linkable")
    @Override
    public List<Linkable> discover() {
        return ResponseHandler.transform( role(AccountRepository.class).all(), AccountTransformer.INSTANCE );
    }

}
