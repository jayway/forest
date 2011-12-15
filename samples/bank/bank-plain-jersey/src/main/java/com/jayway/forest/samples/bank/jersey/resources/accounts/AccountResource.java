package com.jayway.forest.samples.bank.jersey.resources.accounts;

import static com.jayway.forest.core.RoleManager.role;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.jayway.forest.exceptions.NotFoundException;
import com.jayway.forest.roles.Template;
import com.jayway.forest.samples.bank.dto.AccountLinkable;
import com.jayway.forest.samples.bank.dto.TransferToDTO;
import com.jayway.forest.samples.bank.jersey.constraints.DepositAllowed;
import com.jayway.forest.samples.bank.jersey.constraints.HasCredit;
import com.jayway.forest.samples.bank.jersey.constraints.IsWithdrawable;
import com.jayway.forest.samples.bank.model.Account;
import com.jayway.forest.samples.bank.model.AccountManager;
import com.jayway.forest.samples.bank.model.Depositable;
import com.jayway.forest.samples.bank.model.Withdrawable;
import com.jayway.forest.samples.bank.repository.AccountRepository;

public class AccountResource {

    private Account account;

    public AccountResource(Account account) {
        if ( account == null ) throw new NotFoundException();
        this.account = account;
    }

    private AccountLinkable convertAccount() {
//        return new AccountLinkable( account.getAccountNumber(), account.getName(), account.getDescription(), account.getBalance() );
    	return null;
    }

    //public void update( @Template("convertAccount") AccountLinkable account ) {
    //}


    @Path("allowexceeddepositlimit")
    @PUT
    public void allowexceeddepositlimit(@FormParam("allow") Boolean allow ) {
    	account.setAllowExceedBalanceLimit(allow);
    }

    @DepositAllowed
    @Path("deposit")
    @PUT
    public void deposit(@FormParam("amount") Integer amount ) {
        role(AccountManager.class).deposit((Depositable) account, amount);
    }

    @HasCredit
    @IsWithdrawable
    @Path("withdraw")
    @PUT
    public void withdraw(@FormParam("amount") Integer amount ) {
        role(AccountManager.class).withdraw((Withdrawable) account, amount);
    }

    @HasCredit
    @IsWithdrawable
    @Path("transfer")
    @PUT
    public void transfer(TransferToDTO transfer) {
        Depositable depositable = role(AccountRepository.class).findWithRole(transfer.getDestinationAccount(), Depositable.class);
        Withdrawable withdrawable = (Withdrawable) account;

        role(AccountManager.class).transfer(withdrawable, depositable, transfer.getAmount() );
    }

    @Path("changedescription")
    @PUT
    public void changedescription( @Template("accountDescription") String description ) {
        account.setDescription(description);
    }

    @Path("changedescription")
    @GET
    public Response changedescription_get() {
    	return Response.status(405).entity(accountDescription()).build();
    }

    private String accountDescription() {
        return account.getDescription();
    }

    @GET
    @Path("description")
    public String description() {
        return String.format( Account.HTML_DESCRIPTION, account.getAccountNumber(), account.getBalance(), account.isAllowExceedBalanceLimit() );
    }
}
