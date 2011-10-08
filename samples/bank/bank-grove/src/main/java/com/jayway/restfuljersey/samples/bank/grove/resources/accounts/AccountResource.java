package com.jayway.restfuljersey.samples.bank.grove.resources.accounts;

import com.jayway.forest.core.RoleManager;
import com.jayway.forest.roles.DescribedResource;
import com.jayway.forest.roles.Resource;
import com.jayway.restfuljersey.samples.bank.dto.AccountLinkable;
import com.jayway.restfuljersey.samples.bank.dto.TransferToDTO;
import com.jayway.restfuljersey.samples.bank.grove.constraints.DepositAllowed;
import com.jayway.restfuljersey.samples.bank.grove.constraints.HasCredit;
import com.jayway.restfuljersey.samples.bank.grove.constraints.IsWithdrawable;
import com.jayway.restfuljersey.samples.bank.model.Account;
import com.jayway.restfuljersey.samples.bank.model.AccountManager;
import com.jayway.restfuljersey.samples.bank.model.Depositable;
import com.jayway.restfuljersey.samples.bank.model.Withdrawable;
import com.jayway.restfuljersey.samples.bank.repository.AccountRepository;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static com.jayway.forest.core.RoleManager.*;

public class AccountResource implements Resource, DescribedResource {

    private AccountLinkable convertAccount() {
        //Method convertAccount = this.getClass().getDeclaredMethod("convertAccount");
        //if ( Modifier.isPrivate( convertAccount.getModifiers() ) ) do...
        //convertAccount.setAccessible( true );
        //Object object = convertAccount.invoke(this);
        //if ( field.getClass().isAssignableFrom( object.getClass()) ) -> fill values
        Account account = role(Account.class);
        return new AccountLinkable( account.getAccountNumber(), account.getName(), account.getBalance() );
    }

    //public void update( @Template("convertAccount") AccountLinkable account ) {
    //}


    public void allowexceeddepositlimit( Boolean allow ) {
    	role(Account.class).setAllowExceedBalanceLimit(allow);
    }

    @DepositAllowed
    public void deposit( Integer amount ) {
        role(AccountManager.class).deposit((Depositable) role(Account.class), amount);
    }

    @HasCredit
    @IsWithdrawable
    public void withdraw( Integer amount ) {
        role(AccountManager.class).withdraw((Withdrawable) role(Account.class), amount);
    }

    @HasCredit
    @IsWithdrawable
    public void transfer( TransferToDTO transfer ) {
        Depositable depositable = role(AccountRepository.class).findWithRole(transfer.getDestinationAccount(), Depositable.class);
        Withdrawable withdrawable = (Withdrawable) role(Account.class);

        role(AccountManager.class).transfer(withdrawable, depositable, transfer.getAmount() );
    }


    @Override
    public Object description() {
        Account account = role(Account.class);
        return String.format( Account.HTML_DESCRIPTION, account.getAccountNumber(), account.getBalance(), account.isAllowExceedBalanceLimit() );
    }
}
