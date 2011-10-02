package com.jayway.restfuljersey.samples.bank.model;

/**
 */
public class CheckingAccount extends Account implements Depositable, Withdrawable {

    public CheckingAccount( String number, String name ) {
        super(number, name);
    }

    public CheckingAccount(String number, int balance, String name ) {
        super(number, name);
        this.balance = balance;
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
