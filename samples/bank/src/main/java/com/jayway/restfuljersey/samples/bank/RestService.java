package com.jayway.restfuljersey.samples.bank;

import com.jayway.jersey.rest.RestfulServlet;
import com.jayway.jersey.rest.resource.ExceptionMapper;
import com.jayway.jersey.rest.resource.Resource;
import com.jayway.jersey.rest.resource.Response;
import com.jayway.restfuljersey.samples.bank.exceptions.CannotDepositException;
import com.jayway.restfuljersey.samples.bank.exceptions.OverdrawException;
import com.jayway.restfuljersey.samples.bank.model.AccountManager;
import com.jayway.restfuljersey.samples.bank.repository.AccountRepository;
import com.jayway.restfuljersey.samples.bank.resources.RootResource;

import javax.servlet.http.HttpServletResponse;

public class RestService extends RestfulServlet {

    @Override
    protected Resource root() {
        return new RootResource();
    }

    @Override
    protected void setupContext() {
        getContextMap().put(AccountRepository.class, new AccountRepository());
        getContextMap().put(AccountManager.class, new AccountManager());
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
