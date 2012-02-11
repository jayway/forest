package com.jayway.forest.samples.bank.grove.constraints;

import com.jayway.forest.legacy.constraint.Constraint;
import com.jayway.forest.legacy.constraint.ConstraintEvaluator;
import com.jayway.forest.legacy.core.RoleManager;
import com.jayway.forest.legacy.roles.Resource;
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
