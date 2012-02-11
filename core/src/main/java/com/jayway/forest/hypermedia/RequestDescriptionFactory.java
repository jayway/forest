package com.jayway.forest.hypermedia;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;

import com.jayway.forest.Sneak;
import com.jayway.forest.roles.Template;

public class RequestDescriptionFactory<T> {

	private final Class<T> resourceClass;

	public static <T> RequestDescriptionFactory<T> create(Class<T> resourceClass) {
		return new RequestDescriptionFactory<T>(resourceClass);
	}

	private RequestDescriptionFactory(Class<T> resourceClass) {
		this.resourceClass = resourceClass;
	}

	public RequestDescription make(T resource, String methodName) throws Exception {
		Method method = getMethod(methodName);
		ParameterDescription[] parameters = new ParameterDescription[method.getParameterTypes().length];
		for (int indx=0; indx<method.getParameterTypes().length; indx++) {
			String name = getParameterName(method, indx);
			Object defaultValue = getDefaultValue(resource, method, indx);
			parameters[indx] = new ParameterDescription(name, defaultValue);
		}
		Link link = HyperMediaResponseFactory.makeLink(method);
		return new RequestDescription(parameters, link);
	}
	
	private static final Set<Class<? extends Annotation>> ANNOTATION_FOR_PARAMETER_NAME = new HashSet<Class<? extends Annotation>>(Arrays.asList(QueryParam.class, FormParam.class));

	private Object getDefaultValue(T resource, Method method, int indx) throws Exception {
		DefaultValue defaultValue = getParameterAnnotation(DefaultValue.class, method, indx);
		if (defaultValue != null) {
			return defaultValue.value();
		}
		Template template = getParameterAnnotation(Template.class, method, indx);
		if (template != null) {
			Method templateMethod = resourceClass.getDeclaredMethod(template.value());
			templateMethod.setAccessible(true);
			try {
				return templateMethod.invoke(resource);
			} catch (InvocationTargetException e) {
				return Sneak.sneakyThrow(e.getCause());
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private static <A extends Annotation> A getParameterAnnotation(Class<A> clazz, Method method, int indx) {
		Annotation[] annotations = method.getParameterAnnotations()[indx];
		for (Annotation annotation : annotations) {
			if (annotation.annotationType().equals(clazz)) {
				return (A) annotation;
			}
		}
		return null;
	}

	private String getParameterName(Method method, int indx) throws Exception {
		Annotation[] annotations = method.getParameterAnnotations()[indx];
		for (Annotation annotation : annotations) {
			if (ANNOTATION_FOR_PARAMETER_NAME.contains(annotation.annotationType())) {
				Method valueMethod = annotation.getClass().getMethod("value");
				return (String) valueMethod.invoke(annotation);
			}
		}
		return "argument" + (indx+1);
	}

	private Method getMethod(String methodName) {
		for (Method method : resourceClass.getDeclaredMethods()) {
			if (method.getName().equals(methodName)) {
				return method;
			}
		}
		throw new IllegalArgumentException("Method not found: " + methodName);
	}
}
