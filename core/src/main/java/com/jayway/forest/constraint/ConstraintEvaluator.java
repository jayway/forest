package com.jayway.forest.constraint;

/**
 */
public interface ConstraintEvaluator<Annotation, Resource> {

    boolean isValid(Annotation annotation, Resource resource);
}
