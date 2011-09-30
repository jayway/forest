package com.jayway.forest.constraint;

import com.jayway.forest.roles.Resource;

/**
 */
public interface ConstraintEvaluator<Annotation, R extends Resource> {

    boolean isValid(Annotation annotation, R resource);
}
