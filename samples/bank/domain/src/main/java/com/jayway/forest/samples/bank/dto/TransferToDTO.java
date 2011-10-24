package com.jayway.forest.samples.bank.dto;

/**
 */
public class TransferToDTO {
    private String destinationAccount;
    private Integer amount;

    public String getDestinationAccount() {
        return destinationAccount;
    }

    public void setDestinationAccount(String destinationAccount) {
        this.destinationAccount = destinationAccount;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
