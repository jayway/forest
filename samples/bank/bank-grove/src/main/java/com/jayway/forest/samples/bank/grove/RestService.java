package com.jayway.forest.samples.bank.grove;

import javax.servlet.http.HttpServletResponse;

import com.jayway.forest.legacy.core.Application;
import com.jayway.forest.legacy.core.RoleManager;
import com.jayway.forest.legacy.di.grove.GroveDependencyInjectionImpl;
import com.jayway.forest.legacy.roles.Resource;
import com.jayway.forest.legacy.servlet.ExceptionMapper;
import com.jayway.forest.legacy.servlet.Response;
import com.jayway.forest.legacy.servlet.RestfulServlet;
import com.jayway.forest.samples.bank.exceptions.CannotDepositException;
import com.jayway.forest.samples.bank.exceptions.OverdrawException;
import com.jayway.forest.samples.bank.grove.resources.RootResource;
import com.jayway.forest.samples.bank.model.AccountManager;
import com.jayway.forest.samples.bank.repository.AccountRepository;

public class RestService extends RestfulServlet {

    private AccountRepository repository;
    private AccountManager manager;

    @Override
	public void init() {
		initForest(new Application() {
            @Override
            public Resource root() {
                return new RootResource();
            }

            @Override
            public void setupRequestContext() {
                RoleManager.addRole(AccountRepository.class, repository );
                RoleManager.addRole(AccountManager.class, manager );
            }

        }, new GroveDependencyInjectionImpl());

        manager = new AccountManager();
        repository = new AccountRepository();
        repository.initializeDummyAccounts( manager );
	}

    @Override
    protected ExceptionMapper exceptionMapper() {
        return new ExceptionMapper() {
            public Response map(Exception e) {
                if ( e instanceof CannotDepositException) {
                    return new Response(HttpServletResponse.SC_CONFLICT, e.getMessage() );
                } else if ( e instanceof OverdrawException) {
                    return new Response(HttpServletResponse.SC_CONFLICT, "Account cannot be overdrawn");
                }
                return null;
            }
        };
    }
}
