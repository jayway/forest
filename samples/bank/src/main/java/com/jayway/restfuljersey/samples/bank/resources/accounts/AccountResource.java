package com.jayway.restfuljersey.samples.bank.resources.accounts;

import com.jayway.jersey.rest.resource.IndexResource;
import com.jayway.jersey.rest.resource.Resource;
import com.jayway.restfuljersey.samples.bank.constraints.DepositAllowed;
import com.jayway.restfuljersey.samples.bank.constraints.HasCredit;
import com.jayway.restfuljersey.samples.bank.dto.TransferToDTO;
import com.jayway.restfuljersey.samples.bank.model.Account;
import com.jayway.restfuljersey.samples.bank.model.AccountManager;
import com.jayway.restfuljersey.samples.bank.model.Depositable;
import com.jayway.restfuljersey.samples.bank.model.Withdrawable;
import com.jayway.restfuljersey.samples.bank.constraints.HasRole;
import com.jayway.restfuljersey.samples.bank.repository.AccountRepository;

/**
 */
public class AccountResource extends Resource implements IndexResource {

    @Override
    public Object index() {
        Account account = role(Account.class);
        return String.format( "<html><h1>Account: %s</h1> Balance = %d <br>Account is allowed to exceed deposit ensured limit: %s</html>", account.getAccountNumber(), account.getBalance(), account.isAllowExceedBalanceLimit() );
    }

    public void allowexceeddepositlimit( Boolean allow ) {
        role(Account.class).setAllowExceedBalanceLimit(allow);
    }

    @DepositAllowed
    public void deposit( Integer amount ) {
        role(AccountManager.class).deposit((Depositable) role(Account.class), amount);
    }

    @HasCredit
    @HasRole( object = Account.class, hasRole = Withdrawable.class )
    public void withdraw( Integer amount ) {
        role(AccountManager.class).withdraw((Withdrawable) role(Account.class), amount);
    }

    @HasCredit
    @HasRole( object = Account.class, hasRole = Withdrawable.class)
    public void transfer( TransferToDTO transfer ) {
        Depositable depositable = role(AccountRepository.class).findWithRole(transfer.getDestinationAccount(), Depositable.class);
        Withdrawable withdrawable = (Withdrawable) role(Account.class);

        role( AccountManager.class ).transfer(withdrawable, depositable, transfer.getAmount() );
    }

}
