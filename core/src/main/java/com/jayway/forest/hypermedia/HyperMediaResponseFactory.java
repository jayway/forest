package com.jayway.forest.hypermedia;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.HttpMethod;

import com.jayway.forest.constraint.Constraint;

public class HyperMediaResponseFactory<R> {

	private final Class<?> resourceClass;

	public static <R> HyperMediaResponseFactory<R> create(Class<R> resourceClass) {
		return new HyperMediaResponseFactory<R>(resourceClass);
	}

	private HyperMediaResponseFactory(Class<R> resourceClass) {
		this.resourceClass = resourceClass;
	}

	public <B> HyperMediaResponse<B> make(R resource, B body, Class<B> bodyClass) {
		try {
			return makeInner(resource, body, bodyClass);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	private <B> HyperMediaResponse<B> makeInner(R resource, B body, Class<B> bodyClass) throws Exception {
		HyperMediaResponse<B> response = new HyperMediaResponse<B>(resourceClass.getName(), body, bodyClass);
		for (Method method : resourceClass.getMethods()) {
			if (!method.getDeclaringClass().equals(Object.class) && !constrained(resource, method)) {
				String uri = method.getName();
				String name = method.getName();
				String documentation = "";
				String httpMethod = findHttpMethod(method);
				response.addLink(new Link(uri, httpMethod, name, documentation));
			}
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private boolean constrained(R resource, Method method) throws Exception {
		for (Annotation annotation : method.getAnnotations()) {
			Constraint constraint = annotation.annotationType().getAnnotation(Constraint.class);
			if (constraint != null && !constraint.value().newInstance().isValid(annotation, resource)) {
				return true;
			}
		}
		return false;
	}

	private String findHttpMethod(Method method) {
		String httpMethod;
		HttpMethod methodAnnotation = getAnnotation(method.getAnnotations(), HttpMethod.class);
		if (methodAnnotation != null) {
			httpMethod = methodAnnotation.value();
		} else  if (method.getReturnType().equals(Void.TYPE)) {
			httpMethod = HttpMethod.POST;
		} else {
			httpMethod = HttpMethod.GET;
		}
		return httpMethod;
	}

	private <T extends Annotation> T getAnnotation(Annotation[] annotations, Class<T> clazz) {
		for (Annotation annotation : annotations) {
			T a = annotation.annotationType().getAnnotation(clazz);
			if (a != null) {
				return a;
			}
		}
		return null;
	}
}
