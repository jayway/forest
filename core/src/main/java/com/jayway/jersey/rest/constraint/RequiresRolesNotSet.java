package com.jayway.jersey.rest.constraint;

import com.jayway.jersey.rest.resource.ContextMap;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(RequiresRolesNotSet.Evaluator.class)
public @interface RequiresRolesNotSet {

    Class<?>[] value();

    class Evaluator implements ConstraintEvaluator<RequiresRolesNotSet, ContextMap>{

        public boolean isValid( RequiresRolesNotSet role, ContextMap map ) {
            for ( Class<?> clazz : role.value() ) {
                if ( map.get( clazz ) != null ) return false;
            }
            return true;
        }

    }


}
