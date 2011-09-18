package com.jayway.restfuljersey.samples.bank.model;

/**
 */
public class Account {

    public static final int MAX_ENSURED_BALANCE = 1000;
    protected int balance;
    private String number;
    private boolean allowExceedBalanceLimit;

    public Account( String number ) {
        balance = 100;
        this.number = number;
        this.allowExceedBalanceLimit = false;
    }

    public int getBalance() {
        return balance;
    }

    public String getAccountNumber() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        return ( o != null && o instanceof Account && ((Account)o).getAccountNumber().equals( number ));
    }

    public boolean isAllowExceedBalanceLimit() {
        return allowExceedBalanceLimit;
    }

    public void setAllowExceedBalanceLimit(boolean allowExceedBalanceLimit) {
        this.allowExceedBalanceLimit = allowExceedBalanceLimit;
    }
}
