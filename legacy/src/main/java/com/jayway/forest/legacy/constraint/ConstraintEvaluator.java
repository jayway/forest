package com.jayway.forest.legacy.constraint;

/**
 */
public interface ConstraintEvaluator<Annotation, Resource> {

    boolean isValid(Annotation annotation, Resource resource);
}
