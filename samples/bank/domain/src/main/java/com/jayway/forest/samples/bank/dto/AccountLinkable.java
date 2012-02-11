package com.jayway.forest.samples.bank.dto;

import com.jayway.forest.legacy.roles.Linkable;

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

    @Override
    public String toString() {
        return super.toString();
    }
}
