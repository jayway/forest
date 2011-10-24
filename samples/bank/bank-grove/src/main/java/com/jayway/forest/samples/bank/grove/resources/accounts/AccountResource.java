package com.jayway.forest.samples.bank.grove.resources.accounts;

import com.jayway.forest.roles.DescribedResource;
import com.jayway.forest.roles.Resource;
import com.jayway.forest.roles.Template;
import com.jayway.forest.samples.bank.dto.TransferToDTO;
import com.jayway.forest.samples.bank.grove.constraints.DepositAllowed;
import com.jayway.forest.samples.bank.grove.constraints.HasCredit;
import com.jayway.forest.samples.bank.grove.constraints.IsWithdrawable;
import com.jayway.forest.samples.bank.model.*;
import com.jayway.forest.samples.bank.repository.AccountRepository;

import java.util.List;

import static com.jayway.forest.core.RoleManager.addRole;
import static com.jayway.forest.core.RoleManager.role;

public class AccountResource implements Resource, DescribedResource {

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
    public Object description() {
        return account;
    }

    private String accountDescription() {
        return account.getDescription();
    }

    public void changedescription( @Template("accountDescription") String description ) {
        account.setDescription(description);
    }

    public List<Transaction> transactions() {
        return role( AccountManager.class ).getTransactionLog( account );
    }
}
