package com.jayway.restfuljersey.samples.bank.grove;

import javax.servlet.http.HttpServletResponse;

import com.jayway.forest.grove.RoleManager;
import com.jayway.jersey.rest.RestfulServlet;
import com.jayway.jersey.rest.resource.ExceptionMapper;
import com.jayway.jersey.rest.resource.Resource;
import com.jayway.jersey.rest.resource.ResourceUtil;
import com.jayway.jersey.rest.resource.Response;
import com.jayway.jersey.rest.resource.grove.GroveContextMap;
import com.jayway.restfuljersey.samples.bank.exceptions.CannotDepositException;
import com.jayway.restfuljersey.samples.bank.exceptions.OverdrawException;
import com.jayway.restfuljersey.samples.bank.grove.resources.RootResource;
import com.jayway.restfuljersey.samples.bank.model.AccountManager;
import com.jayway.restfuljersey.samples.bank.repository.AccountRepository;

public class RestService extends RestfulServlet {
	
	private final ResourceUtil resourceUtil = new ResourceUtil(new GroveContextMap());

    @Override
    protected Resource root() {
        return new RootResource();
    }

    @Override
    protected void setupContext() {
        RoleManager.addRole(AccountRepository.class, new AccountRepository());
        RoleManager.addRole(AccountManager.class, new AccountManager());
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

	@Override
	protected ResourceUtil resourceUtil() {
		return resourceUtil;
	}
}
