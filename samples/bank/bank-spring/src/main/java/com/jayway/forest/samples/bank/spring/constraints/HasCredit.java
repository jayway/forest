package com.jayway.forest.samples.bank.spring.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jayway.forest.legacy.constraint.Constraint;
import com.jayway.forest.legacy.constraint.ConstraintEvaluator;
import com.jayway.forest.samples.bank.model.Account;
import com.jayway.forest.samples.bank.spring.ResourceWithAccount;

/**
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(HasCredit.Evaluator.class)
public @interface HasCredit {

    class Evaluator implements ConstraintEvaluator<HasCredit, ResourceWithAccount> {

        public boolean isValid( HasCredit role, ResourceWithAccount resource ) {
            Account account = resource.getAccount();
            return account != null && account.getBalance() > 0;
        }

    }

}
