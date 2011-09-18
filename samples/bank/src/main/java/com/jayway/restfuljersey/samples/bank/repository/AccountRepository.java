package com.jayway.restfuljersey.samples.bank.repository;

import com.jayway.restfuljersey.samples.bank.exceptions.NotFoundException;
import com.jayway.restfuljersey.samples.bank.model.Account;
import com.jayway.restfuljersey.samples.bank.model.CheckingAccount;
import com.jayway.restfuljersey.samples.bank.model.SavingsAccount;

import java.util.*;

/**
 */
public class AccountRepository {

    public Account findById( String id ) {
        Account account = accounts.get(id);
        if ( account == null ) {
            throw new NotFoundException();
        }
        return account;
    }

    public Account findWithRole(String id, Class<?> role) {
        Account account = findById(id);
        if ( !role.isAssignableFrom( account.getClass() )) {
            throw new NotFoundException();
        }
        return account;
    }

    public Collection<Account> withRole( Class<?> role ) {
        ArrayList<Account> result = new ArrayList<Account>();
        for ( Account account : accounts.values() ) {
            if ( role.isAssignableFrom( account.getClass() ) ) {
                result.add( account  );
            }
        }
        return result;
    }

    public Collection<Account> all() {
        return accounts.values();
    }

    public static Map<String, Account> accounts;

    static {
        accounts = new HashMap<String, Account>();
        accounts.put("12345", new SavingsAccount( "12345" ) );
        accounts.put("11111", new SavingsAccount("11111"));
        accounts.put("12321", new CheckingAccount("12321"));
        accounts.put("47291", new CheckingAccount("47291"));
    }

}
