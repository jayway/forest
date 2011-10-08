package com.jayway.forest.constraint;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Constraint {
    @SuppressWarnings("rawtypes")
	Class<? extends ConstraintEvaluator> value();
}
