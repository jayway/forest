package com.jayway.forest.samples.bank.model;

/**
 */
public class Account {

    public static final String HTML_DESCRIPTION = "<html><h1>Account: %s</h1> Balance = %d <br>Account is allowed to exceed deposit ensured limit: %s</html>";
    public static final int MAX_ENSURED_BALANCE = 1000;
    protected int balance;
    protected boolean canOverdraw = false;
    private String number;
    private String name;
    private String description;
    private boolean allowExceedBalanceLimit;

    public Account( String number, String name ) {
        this.number = number;
        this.allowExceedBalanceLimit = false;
        this.name = name;
    }

    public Account( String number, String name, Boolean canOverdraw ) {
        this(number, name);
        this.canOverdraw = canOverdraw;
    }

    public void increaseBalance( Integer amount ) {
        balance += amount;
    }

    public void decreaseBalance( Integer amount ) {
        balance -= amount;
    }

    public int getBalance() {
        return balance;
    }

    public String getAccountNumber() {
        return number;
    }

    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription( String description ) {
        this.description = description;
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
