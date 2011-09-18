package com.jayway.restfuljersey.samples.bank.model;

/**
 */
public class AccountManager {

    public void transfer(Withdrawable withdrawable, Depositable depositable, Integer amount) {
        withdrawable.withdraw( amount );
        depositable.deposit( amount );
    }

    public void withdraw( Withdrawable withdrawable, Integer amount ) {
        withdrawable.withdraw( amount );
    }

    public void deposit(Depositable depositable, Integer amount) {
        depositable.deposit( amount );
    }
}
