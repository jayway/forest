package com.jayway.restfuljersey.samples.bank.repository;

import com.jayway.forest.exceptions.NotFoundException;
import com.jayway.restfuljersey.samples.bank.model.Account;
import com.jayway.restfuljersey.samples.bank.model.CheckingAccount;
import com.jayway.restfuljersey.samples.bank.model.SavingsAccount;

import java.util.*;

/**
 */
public class AccountRepository {

    public Account findById( String id ) {
        return accounts.get(id);
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
        accounts.put("11111", new SavingsAccount( "11111", "First account" ) );
        accounts.put("22222", new SavingsAccount("22222", "Second account"));
        accounts.put("33333", new CheckingAccount("33333", "Third account"));
        accounts.put("44444", new CheckingAccount("44444", "Fourth account"));
        accounts.put("5", new CheckingAccount("5", -100, "Overdrawn"));
        accounts.put("6", new CheckingAccount("6", -200, "More overdrawn"));
        accounts.put("7", new CheckingAccount("7",- 150, "Salary1"));
        accounts.put("8", new CheckingAccount("8", -33, "Salary1"));
        accounts.put("9", new CheckingAccount("9", -1000, "Salary2"));
        accounts.put("10", new CheckingAccount("10", -1723, "Salary3"));
        accounts.put("11", new CheckingAccount("11", -323, "Salary4"));
        accounts.put("12", new CheckingAccount("12", -23, "Salary5"));
        accounts.put("13", new CheckingAccount("13", -322, "Salary6"));
        accounts.put("14", new CheckingAccount("14", -2311, "Salary7"));
        accounts.put("15", new CheckingAccount("15", -100, "Salary8"));
        accounts.put("16", new CheckingAccount("16", -100, "Salary9"));
        accounts.put("17", new CheckingAccount("17", -230, "Salary10"));
    }

}
