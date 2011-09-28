package com.jayway.restfuljersey.samples.bank;

import javax.servlet.http.HttpServletResponse;

import com.jayway.forest.grove.RoleManager;
import com.jayway.jersey.rest.RestfulServlet;
import com.jayway.jersey.rest.resource.ExceptionMapper;
import com.jayway.jersey.rest.resource.Resource;
import com.jayway.jersey.rest.resource.Response;
import com.jayway.restfuljersey.samples.bank.exceptions.CannotDepositException;
import com.jayway.restfuljersey.samples.bank.exceptions.OverdrawException;
import com.jayway.restfuljersey.samples.bank.model.AccountManager;
import com.jayway.restfuljersey.samples.bank.repository.AccountRepository;
import com.jayway.restfuljersey.samples.bank.resources.RootResource;

public class RestService extends RestfulServlet {

    @Override
    protected Resource root() {
        return new RootResource();
    }

    @Override
    protected void setupContext() {
        RoleManager.addToContext(AccountRepository.class, new AccountRepository());
        RoleManager.addToContext(AccountManager.class, new AccountManager());
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
