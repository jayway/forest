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
@Constraint(HasCredit.Evaluator.class)
public @interface HasCredit {

    class Evaluator implements ConstraintEvaluator<HasCredit, Resource> {

        public boolean isValid( HasCredit role, Resource resource ) {
            Account account = RoleManager.role(Account.class);
            return account != null && account.getBalance() > 0;
        }

    }

}
