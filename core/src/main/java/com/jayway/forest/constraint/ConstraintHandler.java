package com.jayway.forest.constraint;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;


public final class ConstraintHandler {
	private ConstraintHandler() {}
	
	@SuppressWarnings("unchecked")
	public static boolean constrained(Object resource, Method method) throws Exception {
		for (Annotation annotation : method.getAnnotations()) {
			Constraint constraint = annotation.annotationType().getAnnotation(Constraint.class);
			if (constraint != null && !constraint.value().newInstance().isValid(annotation, resource)) {
				return true;
			}
		}
		return false;
	}
}
