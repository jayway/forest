package com.jayway.restfuljersey.samples.bank;

import com.jayway.jersey.rest.RestfulJerseyService;
import com.jayway.jersey.rest.resource.Resource;
import com.jayway.restfuljersey.samples.bank.model.AccountManager;
import com.jayway.restfuljersey.samples.bank.repository.AccountRepository;
import com.jayway.restfuljersey.samples.bank.resources.RootResource;

import javax.ws.rs.Path;

/**
 */
@Path("rest")
public class RestService extends RestfulJerseyService {

    @Override
    protected Resource root() {
        return new RootResource();
    }

    @Override
    protected void setupContext() {
        getContextMap().put(AccountRepository.class, new AccountRepository());
        getContextMap().put(AccountManager.class, new AccountManager());
    }

}
