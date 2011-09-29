package com.jayway.restfuljersey.samples.bank.grove.resources.accounts;

import static com.jayway.forest.grove.RoleManager.context;

import com.jayway.jersey.rest.resource.Resource;
import com.jayway.jersey.rest.roles.DescribedResource;
import com.jayway.restfuljersey.samples.bank.dto.TransferToDTO;
import com.jayway.restfuljersey.samples.bank.grove.constraints.DepositAllowed;
import com.jayway.restfuljersey.samples.bank.grove.constraints.HasCredit;
import com.jayway.restfuljersey.samples.bank.grove.constraints.IsWithdrawable;
import com.jayway.restfuljersey.samples.bank.model.Account;
import com.jayway.restfuljersey.samples.bank.model.AccountManager;
import com.jayway.restfuljersey.samples.bank.model.Depositable;
import com.jayway.restfuljersey.samples.bank.model.Withdrawable;
import com.jayway.restfuljersey.samples.bank.repository.AccountRepository;

public class AccountResource implements Resource, DescribedResource {

    public void allowexceeddepositlimit( Boolean allow ) {
    	context(Account.class).setAllowExceedBalanceLimit(allow);
    }

    @DepositAllowed
    public void deposit( Integer amount ) {
        context(AccountManager.class).deposit((Depositable) context(Account.class), amount);
    }

    @HasCredit
    @IsWithdrawable
    public void withdraw( Integer amount ) {
        context(AccountManager.class).withdraw((Withdrawable) context(Account.class), amount);
    }

    @HasCredit
    @IsWithdrawable
    public void transfer( TransferToDTO transfer ) {
        Depositable depositable = context(AccountRepository.class).findWithRole(transfer.getDestinationAccount(), Depositable.class);
        Withdrawable withdrawable = (Withdrawable) context(Account.class);

        context(AccountManager.class).transfer(withdrawable, depositable, transfer.getAmount() );
    }


    @Override
    public Object description() {
        Account account = context(Account.class);
        return String.format( Account.HTML_DESCRIPTION, account.getAccountNumber(), account.getBalance(), account.isAllowExceedBalanceLimit() );
    }
}
