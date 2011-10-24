package com.jayway.forest.samples.bank.model;

/**
 */
public class CheckingAccount extends Account implements Depositable, Withdrawable {

    public CheckingAccount( String number, String name, Boolean canOverdraw ) {
        super( number, name, canOverdraw );
    }

    @Override
    public void deposit(int amount) {
        increaseBalance( amount );
    }

    @Override
    public void withdraw(int amount) {
        decreaseBalance( amount );
    }
}
