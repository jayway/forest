package com.jayway.restfuljersey.samples.bank.grove;

import javax.servlet.http.HttpServletResponse;

import com.jayway.forest.core.Application;
import com.jayway.forest.di.grove.GroveDependencyInjectionImpl;
import com.jayway.forest.grove.RoleManager;
import com.jayway.forest.reflection.Transformer;
import com.jayway.forest.roles.Linkable;
import com.jayway.forest.roles.Resource;
import com.jayway.forest.servlet.ExceptionMapper;
import com.jayway.forest.servlet.Response;
import com.jayway.forest.servlet.RestfulServlet;
import com.jayway.restfuljersey.samples.bank.dto.AccountLinkable;
import com.jayway.restfuljersey.samples.bank.exceptions.CannotDepositException;
import com.jayway.restfuljersey.samples.bank.exceptions.OverdrawException;
import com.jayway.restfuljersey.samples.bank.grove.resources.RootResource;
import com.jayway.restfuljersey.samples.bank.model.Account;
import com.jayway.restfuljersey.samples.bank.model.AccountManager;
import com.jayway.restfuljersey.samples.bank.repository.AccountRepository;

import java.util.HashMap;
import java.util.Map;

public class RestService extends RestfulServlet {

	public RestService() {
		super(new Application() {
			@Override
			public Resource root() {
				return new RootResource();
			}

			@Override
			public void setupRequestContext() {
		        RoleManager.addRole(AccountRepository.class, new AccountRepository());
		        RoleManager.addRole(AccountManager.class, new AccountManager());
			}

            @Override
            public Map<Class, Transformer> transformers() {
                Map<Class, Transformer> map = new HashMap<Class, Transformer>();

                map.put( Account.class, new Transformer<Account>() {

                    @Override
                    public Linkable transform(Account account) {
                        return new AccountLinkable( account.getAccountNumber(), account.getName(), account.getBalance() );
                    }
                });
                return map;
            }
        }, new GroveDependencyInjectionImpl());
	}

    @Override
    protected ExceptionMapper exceptionMapper() {
        return new ExceptionMapper() {
            public Response map(Exception e) {
                if ( e instanceof CannotDepositException) {
                    return new Response(HttpServletResponse.SC_CONFLICT, "Deposit not allowed" );
                } else if ( e instanceof OverdrawException) {
                    return new Response(HttpServletResponse.SC_CONFLICT, "Account cannot be overdrawn");
                }
                return null;
            }
        };
    }
}
