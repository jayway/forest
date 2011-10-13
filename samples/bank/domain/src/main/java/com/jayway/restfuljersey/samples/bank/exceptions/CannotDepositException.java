package com.jayway.restfuljersey.samples.bank.exceptions;

/**
 */
public class CannotDepositException extends RuntimeException {
    public CannotDepositException(){}

    public CannotDepositException( String message ) {
        super(message);
    }
}
