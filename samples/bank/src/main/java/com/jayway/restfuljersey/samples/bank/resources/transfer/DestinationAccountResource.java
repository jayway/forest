package com.jayway.restfuljersey.samples.bank.resources.transfer;

import com.jayway.jersey.rest.resource.Resource;
import com.jayway.restfuljersey.samples.bank.model.AccountManager;
import com.jayway.restfuljersey.samples.bank.model.Depositable;
import com.jayway.restfuljersey.samples.bank.model.Withdrawable;

/**
 */
public class DestinationAccountResource extends Resource {

    public void amount( Integer amount ) {
        Withdrawable withdrawable = role(Withdrawable.class);
        Depositable depositable = role(Depositable.class);

        role( AccountManager.class ).transfer( withdrawable, depositable, amount );
    }

}
