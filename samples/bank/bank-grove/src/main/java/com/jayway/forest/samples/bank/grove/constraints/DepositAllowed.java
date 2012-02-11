package com.jayway.forest.samples.bank.grove.constraints;

import com.jayway.forest.legacy.constraint.Constraint;
import com.jayway.forest.legacy.constraint.ConstraintEvaluator;
import com.jayway.forest.legacy.core.RoleManager;
import com.jayway.forest.legacy.roles.Resource;
import com.jayway.forest.samples.bank.model.Account;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(DepositAllowed.Evaluator.class)
public @interface DepositAllowed {

    class Evaluator implements ConstraintEvaluator<DepositAllowed, Resource> {

        public boolean isValid( DepositAllowed role, Resource resource) {
            Account account = RoleManager.role( Account.class );
            if ( account == null ) return false;

            if ( account.getBalance() >= Account.MAX_ENSURED_BALANCE ) {
                return account.isAllowExceedBalanceLimit();
            } else {
                return true;
            }
        }

    }

}
