package com.jayway.forest.constraint.grove;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jayway.forest.constraint.Constraint;
import com.jayway.forest.constraint.ConstraintEvaluator;
import com.jayway.forest.grove.RoleManager;
import com.jayway.forest.roles.Resource;

/**
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(RequiresRolesNotSet.Evaluator.class)
public @interface RequiresRolesNotSet {

    Class<?>[] value();

    class Evaluator implements ConstraintEvaluator<RequiresRolesNotSet, Resource>{

        public boolean isValid( RequiresRolesNotSet role, Resource resource ) {
            for ( Class<?> clazz : role.value() ) {
                if ( RoleManager.role( clazz ) != null ) return false;
            }
            return true;
        }

    }


}
