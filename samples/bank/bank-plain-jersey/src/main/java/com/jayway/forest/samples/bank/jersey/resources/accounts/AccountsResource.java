package com.jayway.forest.samples.bank.jersey.resources.accounts;

import static com.jayway.forest.core.RoleManager.role;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.jayway.forest.constraint.Doc;
import com.jayway.forest.roles.Linkable;
import com.jayway.forest.samples.bank.dto.AccountLinkable;
import com.jayway.forest.samples.bank.dto.AccountTransformer;
import com.jayway.forest.samples.bank.model.Account;
import com.jayway.forest.samples.bank.model.CheckingAccount;
import com.jayway.forest.samples.bank.repository.AccountRepository;
import com.jayway.forest.servlet.ResponseHandler;

public class AccountsResource {

	@Path("search")
    @Doc("Dummy for testing list queries with argument")
	@GET
    public List<Linkable> search( String name ) {
        List<Linkable> list = new ArrayList<Linkable>();
        list.add( new Linkable( name + "/", name));
        list.add( new Linkable( "other"+name + "/", "other"+name));
        return list;
    }

    @Doc("returning a list of something not Linkable")
	@Path("overdrawscheckingaccounts")
	@GET
    public List<CheckingAccount> overdrawscheckingaccounts() {
        List<CheckingAccount> overdrawn = new ArrayList<CheckingAccount>();
        for (Account account : role(AccountRepository.class).all()) {
            if ( account instanceof CheckingAccount && account.getBalance() < 0 ) overdrawn.add((CheckingAccount) account);
        }
        return overdrawn;
    }

    @Doc("returning a list of AccountLinkable")
	@Path("overdrawn")
	@GET
    public List<AccountLinkable> overdrawn() {
        List<AccountLinkable> overdrawn = new ArrayList<AccountLinkable>();
        for (Account account : role(AccountRepository.class).all()) {
            if ( account.getBalance() < 0 ) overdrawn.add( new AccountLinkable( account.getAccountNumber(), account.getName(), account.getDescription(), account.getBalance() ));
        }
        return overdrawn;
    }

	@Path("{id}/")
    public AccountResource id(@PathParam("id") String id) {
        Account account = role(AccountRepository.class).findById(id);
        return new AccountResource( account );
    }

    @Doc("returning a list of Linkable")
	@Path("discover")
	@GET
    public List<Linkable> discover() {
        return ResponseHandler.transform( role(AccountRepository.class).all(), AccountTransformer.INSTANCE );
    }
}
