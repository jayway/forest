package com.jayway.restfuljersey.samples.bank.jersey.resources.accounts;

import static com.jayway.forest.core.RoleManager.role;

import com.jayway.forest.exceptions.NotFoundException;
import com.jayway.forest.roles.DescribedResource;
import com.jayway.forest.roles.ReadableResource;
import com.jayway.forest.roles.Resource;
import com.jayway.forest.samples.bank.dto.AccountLinkable;
import com.jayway.forest.samples.bank.dto.TransferToDTO;
import com.jayway.forest.samples.bank.model.Account;
import com.jayway.forest.samples.bank.model.AccountManager;
import com.jayway.forest.samples.bank.model.Depositable;
import com.jayway.forest.samples.bank.model.Withdrawable;
import com.jayway.forest.samples.bank.repository.AccountRepository;
import com.jayway.restfuljersey.samples.bank.jersey.constraints.DepositAllowed;
import com.jayway.restfuljersey.samples.bank.jersey.constraints.HasCredit;
import com.jayway.restfuljersey.samples.bank.jersey.constraints.IsWithdrawable;

public class AccountResource implements Resource, ReadableResource<String> {

    private Account account;

    public AccountResource(Account account) {
        if ( account == null ) throw new NotFoundException();
        this.account = account;
    }

    private AccountLinkable convertAccount() {
        return new AccountLinkable( account.getAccountNumber(), account.getName(), account.getBalance() );
    }

    //public void update( @Template("convertAccount") AccountLinkable account ) {
    //}


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
    public String read() {
        return String.format( Account.HTML_DESCRIPTION, account.getAccountNumber(), account.getBalance(), account.isAllowExceedBalanceLimit() );
    }
}
