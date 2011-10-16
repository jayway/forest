package com.jayway.restfuljersey.samples.bank.dto;

import com.jayway.forest.roles.Linkable;
import com.jayway.forest.servlet.ResponseHandler;
import com.jayway.restfuljersey.samples.bank.model.Account;

/**
 */
public class AccountTransformer implements ResponseHandler.Transform<Account> {

    public static final AccountTransformer INSTANCE = new AccountTransformer();

    private AccountTransformer() {}

    public Linkable transform(Account account) {

        return new AccountLinkable( account.getAccountNumber(), account.getName(), account.getName(), account.getBalance() );
    }
}
