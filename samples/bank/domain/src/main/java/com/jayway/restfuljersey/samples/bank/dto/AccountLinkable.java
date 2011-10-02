package com.jayway.restfuljersey.samples.bank.dto;

import com.jayway.forest.roles.Linkable;

/**
 */
public class AccountLinkable extends Linkable {

    private int balance;

    public AccountLinkable(String id, String name, int balance) {
        super(id, name);
        this.balance = balance;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}
