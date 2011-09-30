package com.jayway.jersey.rest.constraint;

import com.jayway.jersey.rest.resource.Resource;

/**
 */
public interface ConstraintEvaluator<Annotation, R extends Resource> {

    boolean isValid(Annotation annotation, R resource);
}
