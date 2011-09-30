package com.jayway.restfuljersey.samples.bank.grove.resources.accounts;

import static com.jayway.forest.grove.RoleManager.addRole;
import static com.jayway.forest.grove.RoleManager.role;

import java.util.List;

import com.jayway.forest.roles.IdDiscoverableResource;
import com.jayway.forest.roles.Linkable;
import com.jayway.forest.roles.Resource;
import com.jayway.forest.servlet.ResponseHandler;
import com.jayway.restfuljersey.samples.bank.model.Account;
import com.jayway.restfuljersey.samples.bank.repository.AccountRepository;

public class AccountsResource implements Resource, IdDiscoverableResource {

    @Override
    public Resource id(String id) {
        Account account = role(AccountRepository.class).findById(id);
        addRole(Account.class, account);
        return new AccountResource();
    }

    @Override
    public List<Linkable> discover() {
        return ResponseHandler.mapList(Account.class, role(AccountRepository.class).all(), "number", "number" );
    }
}
