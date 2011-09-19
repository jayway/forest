package com.jayway.restfuljersey.samples.bank.constraints;

import com.jayway.jersey.rest.constraint.Constraint;
import com.jayway.jersey.rest.constraint.ConstraintEvaluator;
import com.jayway.jersey.rest.resource.ContextMap;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(HasRole.Evaluator.class)
public @interface HasRole {

    Class<?> object();
    Class<?> hasRole();

    class Evaluator implements ConstraintEvaluator<HasRole, ContextMap> {

        public boolean isValid( HasRole role, ContextMap map ) {
            Class<?> instance = role.object();
            Class<?> theRole = role.hasRole();

            Object theInstance = map.get( instance );
            return theRole.isAssignableFrom( theInstance.getClass() );
        }

    }

}
