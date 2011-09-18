package com.jayway.restfuljersey.samples.bank.model;

/**
 */
public class SavingsAccount extends Account implements Depositable {

    public SavingsAccount(String number) {
        super(number);
    }

    @Override
    public void deposit(int amount) {
        balance += amount;
    }
}
