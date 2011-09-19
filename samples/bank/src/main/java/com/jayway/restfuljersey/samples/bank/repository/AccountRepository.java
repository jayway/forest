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

    public <T> T findWithRole(String id, Class<T> role) {
        Account account = findById(id);
        if ( !role.isAssignableFrom( account.getClass() )) {
            throw new NotFoundException();
        }
        return (T) account;
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
        accounts = new LinkedHashMap<String, Account>();
        accounts.put("11111", new SavingsAccount( "11111" ) );
        accounts.put("22222", new SavingsAccount("22222"));
        accounts.put("33333", new CheckingAccount("33333"));
        accounts.put("44444", new CheckingAccount("44444"));
    }

}
