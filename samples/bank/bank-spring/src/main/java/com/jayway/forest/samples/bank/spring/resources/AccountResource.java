package com.jayway.forest.samples.bank.spring.resources;

import com.jayway.forest.roles.ReadableResource;
import org.springframework.beans.factory.annotation.Autowired;

import com.jayway.forest.roles.DescribedResource;
import com.jayway.forest.roles.Resource;
import com.jayway.forest.samples.bank.dto.TransferToDTO;
import com.jayway.forest.samples.bank.model.Account;
import com.jayway.forest.samples.bank.model.AccountManager;
import com.jayway.forest.samples.bank.model.Depositable;
import com.jayway.forest.samples.bank.model.Withdrawable;
import com.jayway.forest.samples.bank.repository.AccountRepository;
import com.jayway.forest.samples.bank.spring.ResourceWithAccount;
import com.jayway.forest.samples.bank.spring.constraints.DepositAllowed;
import com.jayway.forest.samples.bank.spring.constraints.HasCredit;
import com.jayway.forest.samples.bank.spring.constraints.IsWithdrawable;

import static com.jayway.forest.core.RoleManager.*;

public class AccountResource implements Resource, ReadableResource<String>, ResourceWithAccount {

    private final Account account;
    
    @Autowired
    private AccountManager accountManager;

	public AccountResource(Account account) {
		this.account = account;
	}

	public void allowexceeddepositlimit( Boolean allow ) {
    	account.setAllowExceedBalanceLimit(allow);
    }

    @DepositAllowed
    public void deposit( Integer amount ) {
        accountManager.deposit((Depositable) account, amount);
    }

    @HasCredit
    @IsWithdrawable
    public void withdraw( Integer amount ) {
        accountManager.withdraw((Withdrawable) account, amount);
    }

    @HasCredit
    @IsWithdrawable
    public void transfer( TransferToDTO transfer ) {
        Depositable depositable = role(AccountRepository.class).findWithRole(transfer.getDestinationAccount(), Depositable.class);
        Withdrawable withdrawable = (Withdrawable) account;

        accountManager.transfer(withdrawable, depositable, transfer.getAmount() );
    }


    @Override
    public String read() {
        return String.format( Account.HTML_DESCRIPTION, account.getAccountNumber(), account.getBalance(), account.isAllowExceedBalanceLimit() );
    }

	@Override
	public Account getAccount() {
		return account;
	}
}
