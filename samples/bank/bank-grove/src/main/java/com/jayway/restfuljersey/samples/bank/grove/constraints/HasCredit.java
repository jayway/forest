package com.jayway.restfuljersey.samples.bank.grove.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jayway.forest.constraint.Constraint;
import com.jayway.forest.constraint.ConstraintEvaluator;
import com.jayway.forest.grove.RoleManager;
import com.jayway.forest.roles.Resource;
import com.jayway.restfuljersey.samples.bank.model.Account;

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
