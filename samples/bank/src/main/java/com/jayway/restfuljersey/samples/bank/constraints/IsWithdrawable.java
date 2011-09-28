package com.jayway.restfuljersey.samples.bank.constraints;

import com.jayway.jersey.rest.constraint.Constraint;
import com.jayway.jersey.rest.constraint.ConstraintEvaluator;
import com.jayway.jersey.rest.resource.ContextMap;
import com.jayway.restfuljersey.samples.bank.model.Account;
import com.jayway.restfuljersey.samples.bank.model.Withdrawable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(IsWithdrawable.Evaluator.class)
public @interface IsWithdrawable {

    class Evaluator implements ConstraintEvaluator<IsWithdrawable, ContextMap> {

        public boolean isValid( IsWithdrawable role, ContextMap map ) {
            return map.role(Account.class) instanceof Withdrawable;
        }

    }

}
