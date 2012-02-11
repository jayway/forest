package com.jayway.forest.samples.bank.spring.constraints;

import java.lang.annotation.*;

import com.jayway.forest.legacy.constraint.Constraint;
import com.jayway.forest.legacy.constraint.ConstraintEvaluator;
import com.jayway.forest.samples.bank.model.Account;
import com.jayway.forest.samples.bank.spring.ResourceWithAccount;

/**
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(DepositAllowed.Evaluator.class)
public @interface DepositAllowed {

    class Evaluator implements ConstraintEvaluator<DepositAllowed, ResourceWithAccount> {

        public boolean isValid( DepositAllowed role, ResourceWithAccount resource ) {
            Account account = resource.getAccount();
            if ( account == null ) return false;

            if ( account.getBalance() >= Account.MAX_ENSURED_BALANCE ) {
                return account.isAllowExceedBalanceLimit();
            } else {
                return true;
            }
        }

    }

}
