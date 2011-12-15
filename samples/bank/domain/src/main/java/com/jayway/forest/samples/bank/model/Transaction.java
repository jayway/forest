package com.jayway.forest.samples.bank.model;

import java.util.Date;
import java.util.UUID;

/**
 */
public class Transaction {

    private String id;
    private Date date;
    private Integer amount;
    private String description;
    private Integer balance;

    public Transaction( Integer amount, String description, Integer balance ) {
        this.id = UUID.randomUUID().toString();
        this.amount = amount;
        this.description = description;
        this.date = new Date();
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public Integer getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }
}
