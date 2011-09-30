package com.jayway.restfuljersey.samples.bank.spring.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jayway.forest.constraint.Constraint;
import com.jayway.forest.constraint.ConstraintEvaluator;
import com.jayway.restfuljersey.samples.bank.model.Account;
import com.jayway.restfuljersey.samples.bank.spring.ResourceWithAccount;

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
