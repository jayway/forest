package com.jayway.restfuljersey.samples.bank.model;

import com.jayway.restfuljersey.samples.bank.exceptions.CannotDepositException;
import com.jayway.restfuljersey.samples.bank.exceptions.OverdrawException;

/**
 */
public class AccountManager {

    public void transfer(Withdrawable withdrawable, Depositable depositable, Integer amount) {
        withdraw(withdrawable, amount);
        try {
            deposit( depositable, amount );
        } catch ( CannotDepositException e ) {
            // undo withdraw
            ((Account) withdrawable).balance += amount;
            throw e;
        }
    }

    public void withdraw( Withdrawable withdrawable, Integer amount ) {
        if ( ((Account) withdrawable).balance >= amount ) {
            withdrawable.withdraw( amount );
        } else {
            throw new OverdrawException();
        }
    }

    public void deposit(Depositable depositable, Integer amount) {
        Account account = (Account) depositable;
        if ( account.balance + amount > Account.MAX_ENSURED_BALANCE && !account.isAllowExceedBalanceLimit() ) {
            throw new CannotDepositException( "Trying to deposit "+amount+" cannot be done because that would exceed deposit limit");
        }
        depositable.deposit( amount );
    }
}
