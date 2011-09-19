package com.jayway.restfuljersey.samples.bank.model;

import com.jayway.restfuljersey.samples.bank.exceptions.OverdrawException;

/**
 */
public class CheckingAccount extends Account implements Depositable, Withdrawable {

    public CheckingAccount(String number) {
        super(number);
    }

    @Override
    public void deposit(int amount) {
        balance += amount;
    }

    @Override
    public void withdraw(int amount) {
        balance -= amount;
    }
}
