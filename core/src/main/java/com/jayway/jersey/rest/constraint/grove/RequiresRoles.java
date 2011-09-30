package com.jayway.jersey.rest.constraint.grove;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jayway.forest.grove.RoleManager;
import com.jayway.jersey.rest.constraint.Constraint;
import com.jayway.jersey.rest.constraint.ConstraintEvaluator;
import com.jayway.jersey.rest.resource.Resource;

/**
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(RequiresRoles.Evaluator.class)
public @interface RequiresRoles {

    Class<?>[] value();

    class Evaluator implements ConstraintEvaluator<RequiresRoles, Resource>{

        public boolean isValid( RequiresRoles role, Resource resource ) {
            for ( Class<?> clazz : role.value() ) {
                if ( RoleManager.role( clazz ) == null ) return false;
            }
            return true;
        }

    }

}
