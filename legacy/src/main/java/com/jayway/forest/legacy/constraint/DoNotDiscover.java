package com.jayway.forest.legacy.constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jayway.forest.legacy.roles.Resource;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(DoNotDiscover.Evaluator.class)
public @interface DoNotDiscover {
    class Evaluator implements ConstraintEvaluator<DoNotDiscover, Resource> {
        public boolean isValid( DoNotDiscover role, Resource resource ) {
        	return false;
        }
    }
}
