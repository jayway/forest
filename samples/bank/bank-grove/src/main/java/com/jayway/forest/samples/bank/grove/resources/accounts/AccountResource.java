package com.jayway.forest.samples.bank.grove.resources.accounts;

import com.jayway.forest.legacy.roles.DescribedResource;
import com.jayway.forest.legacy.roles.ReadableResource;
import com.jayway.forest.legacy.roles.Resource;
import com.jayway.forest.legacy.roles.Template;
import com.jayway.forest.samples.bank.dto.TransferToDTO;
import com.jayway.forest.samples.bank.grove.constraints.DepositAllowed;
import com.jayway.forest.samples.bank.grove.constraints.HasCredit;
import com.jayway.forest.samples.bank.grove.constraints.IsWithdrawable;
import com.jayway.forest.samples.bank.model.*;
import com.jayway.forest.samples.bank.repository.AccountRepository;

import java.util.List;

import static com.jayway.forest.legacy.core.RoleManager.addRole;
import static com.jayway.forest.legacy.core.RoleManager.role;

public class AccountResource implements Resource, ReadableResource<Account> {

    private Account account;

    public AccountResource(Account account) {
        addRole(Account.class, account);
        this.account = account;
    }

    public void allowexceeddepositlimit( Boolean allow ) {
    	account.setAllowExceedBalanceLimit(allow);
    }

    @DepositAllowed
    public void deposit( Integer amount ) {
        role(AccountManager.class).deposit((Depositable) account, amount);
    }

    @HasCredit
    @IsWithdrawable
    public void withdraw( Integer amount ) {
        role(AccountManager.class).withdraw((Withdrawable) account, amount);
    }

    @HasCredit
    @IsWithdrawable
    public void transfer( TransferToDTO transfer ) {
        Depositable depositable = role(AccountRepository.class).findWithRole(transfer.getDestinationAccount(), Depositable.class);
        Withdrawable withdrawable = (Withdrawable) account;

        role(AccountManager.class).transfer(withdrawable, depositable, transfer.getAmount() );
    }

    @Override
    public Account read() {
        return account;
    }

    private String accountName() {
        return account.getName();
    }

    public void changename( @Template("accountName") String name ) {
        account.setName(name);
    }

    public List<Transaction> transactions() {
        return role( AccountManager.class ).getTransactionLog( account );
    }
}
