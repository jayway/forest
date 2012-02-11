package com.jayway.forest.legacy.constraint;

import com.jayway.forest.legacy.core.RoleManager;
import com.jayway.forest.legacy.roles.Resource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(RolesNotInContext.Evaluator.class)
public @interface RolesNotInContext {

    Class<?>[] value();

    class Evaluator implements ConstraintEvaluator<RolesNotInContext, Resource>{

        public boolean isValid( RolesNotInContext role, Resource resource ) {
            for ( Class<?> clazz : role.value() ) {
                if ( RoleManager.role(clazz) != null ) return false;
            }
            return true;
        }

    }


}
