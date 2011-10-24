package com.jayway.forest.samples.bank.repository;

import com.jayway.forest.exceptions.NotFoundException;
import com.jayway.forest.samples.bank.model.Account;
import com.jayway.forest.samples.bank.model.AccountManager;
import com.jayway.forest.samples.bank.model.CheckingAccount;
import com.jayway.forest.samples.bank.model.SavingsAccount;

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

    public void initializeDummyAccounts(AccountManager manager) {
        accounts = new LinkedHashMap<String, Account>();
        accounts.put("11111", createSavingsAccount("11111", "First account", manager ) );
        accounts.put("22222", createSavingsAccount("22222", "Second account", manager ));
        accounts.put("33333", createCheckingAccount("33333", "Third account", manager));
        accounts.put("44444", createCheckingAccount("44444", "Fourth account", manager));

        accounts.put("5", createCheckingAccountWithRandomTransactions("5", "Overdrawn", manager, -100));
        accounts.put("6",  createCheckingAccountWithRandomTransactions("6", "More overdrawn", manager, -200));
        accounts.put("7",  createCheckingAccountWithRandomTransactions("7", "Salary1", manager, -150));
        accounts.put("8",  createCheckingAccountWithRandomTransactions("8", "Salary1", manager, -33));
        accounts.put("9",  createCheckingAccountWithRandomTransactions("9", "Salary2", manager, -1000 ));
        accounts.put("10",  createCheckingAccountWithRandomTransactions("10", "Salary3", manager, -1723));
        accounts.put("11",  createCheckingAccountWithRandomTransactions("11", "Salary4", manager,-323));
        accounts.put("12",  createCheckingAccountWithRandomTransactions("12", "Salary5", manager,-23));
        accounts.put("13",  createCheckingAccountWithRandomTransactions("13", "Salary6", manager,-322));
        accounts.put("14",  createCheckingAccountWithRandomTransactions("14", "Salary7", manager,-2311));
        accounts.put("15",  createCheckingAccountWithRandomTransactions("15", "Salary8", manager,-100));
        accounts.put("16",  createCheckingAccountWithRandomTransactions("16", "Salary9", manager,-100));
        accounts.put("17", createCheckingAccountWithRandomTransactions("17", "Salary10", manager,-230));
    }

    private SavingsAccount createSavingsAccount( String accountNumber, String accountName, AccountManager manager ) {
        SavingsAccount account = new SavingsAccount(accountNumber, accountName);
        manager.deposit( account, 100 );
        return account;
    }

    private CheckingAccount createCheckingAccount( String accountNumber, String accountName, AccountManager manager ) {
        return createCheckingAccount(accountNumber, accountName, manager, false);
    }

    private CheckingAccount createCheckingAccount( String accountNumber, String accountName, AccountManager manager, Boolean canOverdraw ) {
        CheckingAccount account = new CheckingAccount(accountNumber, accountName, canOverdraw);
        manager.deposit( account, 100 );
        return account;
    }

    private CheckingAccount createCheckingAccountWithRandomTransactions( String accountNumber, String accountName, AccountManager manager, Integer target ) {
        CheckingAccount account = createCheckingAccount(accountNumber, accountName, manager, true);
        Random random = new Random();
        while ( account.getBalance() > target ) {
            manager.withdraw( account, 1+random.nextInt(50) );
        }
        return account;
    }

}
