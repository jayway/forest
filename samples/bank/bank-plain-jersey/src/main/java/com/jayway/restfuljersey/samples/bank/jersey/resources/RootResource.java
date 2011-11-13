package com.jayway.restfuljersey.samples.bank.jersey.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import com.jayway.forest.core.RoleManager;
import com.jayway.forest.core.Setup;
import com.jayway.forest.roles.Resource;
import com.jayway.forest.samples.bank.model.AccountManager;
import com.jayway.forest.samples.bank.repository.AccountRepository;
import com.jayway.restfuljersey.samples.bank.jersey.resources.accounts.AccountsResource;
import com.sun.jersey.spi.resource.Singleton;

@Path("")
@Singleton
public class RootResource implements Resource {
	
	private final AccountManager accountManager = new AccountManager();
	private final AccountRepository accountRepository = new AccountRepository();
	
	public RootResource() {
        Setup.setup();
		accountRepository.initializeDummyAccounts(accountManager);
	}

	@Path("simpleEcho")
	@GET
	public String simpleEcho(@QueryParam("param") String value) {
		return value;
	}

	@Path("accounts/")
    public AccountsResource accounts() {
		RoleManager.addRole(AccountRepository.class, accountRepository);
		RoleManager.addRole(AccountManager.class, accountManager);
        return new AccountsResource();
    }
}
