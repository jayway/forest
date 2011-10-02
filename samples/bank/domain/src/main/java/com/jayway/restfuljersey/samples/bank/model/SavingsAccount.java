package com.jayway.restfuljersey.samples.bank.model;

/**
 */
public class SavingsAccount extends Account implements Depositable {

    public SavingsAccount(String number, String name) {
        super(number, name);
    }

    @Override
    public void deposit(int amount) {
        balance += amount;
    }
}
