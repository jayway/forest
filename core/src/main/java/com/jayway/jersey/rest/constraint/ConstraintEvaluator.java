package com.jayway.jersey.rest.constraint;

/**
 */
public interface ConstraintEvaluator<Annotation, ContextMap> {

    boolean isValid(Annotation annotation, ContextMap context);
}
