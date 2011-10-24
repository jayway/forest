package com.jayway.forest.samples.bank.model;

import com.jayway.forest.samples.bank.exceptions.CannotDepositException;
import com.jayway.forest.samples.bank.exceptions.OverdrawException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class AccountManager {

    private static Map<String, List<Transaction>> transactionLog = new HashMap<String, List<Transaction>>();

    public void transfer(Withdrawable withdrawable, Depositable depositable, Integer amount) {
        checkWithdraw((Account) withdrawable, amount);
        checkDeposit((Account) depositable, amount);

        doWithdraw( withdrawable, amount, "Transfered "+amount+" to account '"+((Account)depositable).getAccountNumber()+"'" );
        doDeposit( depositable, amount, "Transfered "+amount+" from account '"+((Account)withdrawable).getAccountNumber()+"'" );
    }

    public void withdraw( Withdrawable withdrawable, Integer amount ) {
        checkWithdraw((Account) withdrawable, amount );
        doWithdraw( withdrawable, amount );
    }

    public void deposit(Depositable depositable, Integer amount) {
        checkDeposit((Account) depositable, amount );
        doDeposit( depositable, amount );
    }

    public List<Transaction> getTransactionLog( Account account ) {
        ensureLog( account );
        return transactionLog.get( account.getAccountNumber() );
    }

    private void checkDeposit(Account account, Integer amount) {
        if ( account.balance + amount > Account.MAX_ENSURED_BALANCE && !account.isAllowExceedBalanceLimit() ) {
            throw new CannotDepositException( "Trying to deposit "+amount+" cannot be done because that would exceed deposit limit");
        }
    }

    private void doDeposit( Depositable depositable, Integer amount, String message ) {
        depositable.deposit( amount );
        logTransaction( (Account) depositable, new Transaction( amount, message) );
    }

    private void doDeposit( Depositable depositable, Integer amount ) {
        doDeposit( depositable, amount, "Deposited "+amount );
    }

    private void checkWithdraw(Account account, Integer amount) {
        if ( !account.canOverdraw && account.balance >= amount ) {
            throw new OverdrawException();
        }
    }

    private void doWithdraw( Withdrawable withdrawable, Integer amount, String message ) {
        withdrawable.withdraw( amount );
        logTransaction( (Account) withdrawable, new Transaction( amount, message ) );
    }

    private void doWithdraw( Withdrawable withdrawable, Integer amount ) {
        doWithdraw( withdrawable, amount, "Withdrawn "+amount );
    }

    private void logTransaction( Account account, Transaction transaction ) {
        ensureLog( account );
        transactionLog.get( account.getAccountNumber() ).add( transaction );
    }

    private void ensureLog( Account account ) {
        if ( transactionLog.get( account.getAccountNumber() ) == null ) {
            transactionLog.put( account.getAccountNumber(), new ArrayList<Transaction>());
        }
    }

}
