package com.jayway.restfuljersey.samples.bank.dto;

import com.jayway.forest.roles.Linkable;

/**
 */
public class AccountLinkable extends Linkable {

    private int balance;

    public AccountLinkable(String id, String name, String description, int balance) {
        super(id + "/", name, null, description);
        this.balance = balance;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return super.toString();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
