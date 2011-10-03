package com.jayway.restfuljersey.samples.bank.grove.resources.accounts;

import static com.jayway.forest.grove.RoleManager.addRole;
import static com.jayway.forest.grove.RoleManager.role;

import java.util.ArrayList;
import java.util.List;

import com.jayway.forest.constraint.Doc;
import com.jayway.forest.reflection.SortingParameter;
import com.jayway.forest.roles.IdDiscoverableResource;
import com.jayway.forest.roles.Linkable;
import com.jayway.forest.roles.Resource;
import com.jayway.forest.servlet.ResponseHandler;
import com.jayway.restfuljersey.samples.bank.model.Account;
import com.jayway.restfuljersey.samples.bank.model.CheckingAccount;
import com.jayway.restfuljersey.samples.bank.repository.AccountRepository;

public class AccountsResource implements Resource, IdDiscoverableResource {


    @Doc("returning a list of CheckingAccount that cannot be transformed to Linkable")
    public List<CheckingAccount> overdrawscheckingaccounts() {
        List<CheckingAccount> overdrawn = new ArrayList<CheckingAccount>();
        for (Account account : role(AccountRepository.class).all()) {
            if ( account instanceof CheckingAccount && account.getBalance() < 0 ) overdrawn.add((CheckingAccount) account);
        }
        return overdrawn;
    }

    @Doc("returning a list of Account which can be transformed to AccountLinkable")
    public List<Account> overdrawn() {
        List<Account> overdrawn = new ArrayList<Account>();
        for (Account account : role(AccountRepository.class).all()) {
            if ( account.getBalance() < 0 ) overdrawn.add( account );
        }
        return overdrawn;
    }

    @Override
    public Resource id(String id) {
        Account account = role(AccountRepository.class).findById(id);
        addRole(Account.class, account);
        return new AccountResource();
    }

    @Doc("returning a list of Linkable")
    @Override
    public List<Linkable> discover() {
        return ResponseHandler.mapList(Account.class, role(AccountRepository.class).all(), "number", "name" );
    }
}
