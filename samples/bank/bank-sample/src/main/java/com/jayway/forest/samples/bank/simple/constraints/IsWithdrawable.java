package com.jayway.forest.samples.bank.simple.constraints;

import com.jayway.forest.constraint.Constraint;
import com.jayway.forest.constraint.ConstraintEvaluator;
import com.jayway.forest.core.RoleManager;
import com.jayway.forest.roles.Resource;
import com.jayway.forest.samples.bank.model.Account;
import com.jayway.forest.samples.bank.model.Withdrawable;

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

    class Evaluator implements ConstraintEvaluator<IsWithdrawable, Resource> {

        public boolean isValid( IsWithdrawable role, Resource resource ) {
            return RoleManager.role(Account.class) instanceof Withdrawable;
        }

    }

}
