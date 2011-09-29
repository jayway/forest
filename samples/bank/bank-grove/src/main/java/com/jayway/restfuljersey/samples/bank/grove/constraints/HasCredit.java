package com.jayway.restfuljersey.samples.bank.grove.constraints;

import com.jayway.jersey.rest.constraint.Constraint;
import com.jayway.jersey.rest.constraint.ConstraintEvaluator;
import com.jayway.jersey.rest.resource.ContextMap;
import com.jayway.jersey.rest.resource.Resource;
import com.jayway.restfuljersey.samples.bank.model.Account;

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

        public boolean isValid( HasCredit role, Resource resource, ContextMap map ) {
            Account account = map.role(Account.class);
            return account != null && account.getBalance() > 0;
        }

    }

}
