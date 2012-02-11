package com.jayway.forest.core.javassist;

import java.lang.reflect.Method;
import java.util.List;

import javassist.bytecode.annotation.Annotation;

public abstract class AnnotationUtil {
	private AnnotationUtil() {}
	
	public static boolean contains(List<Annotation> paramAnnotations, String annotationClassName) {
		return find(paramAnnotations, annotationClassName) != null;
	}

	public static Annotation find(List<Annotation> paramAnnotations, String annotationClassName) {
		for (Annotation annotation : paramAnnotations) {
			if (annotation.getTypeName().equals(annotationClassName)) {
				return annotation;
			}
		}
		return null;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static boolean hasAnnotation(Annotation[] annotations, Class annotationClass) throws Exception {
		for (Annotation annotation : annotations) {
			if (Class.forName(annotation.getTypeName()).getAnnotation(annotationClass) != null) {
				return true;
			}
		}
		return false;
	}

	public static String findAnnotationValue(Object[] annotations, Class<?> annotationClass) throws Exception {
		for (Object annotation : annotations) {
			if (annotationClass.isInstance(annotation)) {
				Method method = annotationClass.getMethod("value");
				return (String) method.invoke(annotation);
			}
		}
		return null;
	}
}
