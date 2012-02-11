package com.jayway.forest.hypermedia;

import static com.jayway.forest.constraint.ConstraintHandler.constrained;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import javax.ws.rs.HttpMethod;

import com.jayway.forest.roles.IdDiscoverableResource;

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
	
	@SuppressWarnings("unchecked")
	private <B> HyperMediaResponse<B> makeInner(R resource, B body, Class<B> bodyClass) throws Exception {
		HyperMediaResponse<B> response = new HyperMediaResponse<B>(resourceClass.getName(), body, bodyClass);
		for (Method method : resourceClass.getMethods()) {
			if (!method.getDeclaringClass().equals(Object.class) && !constrained(resource, method)) {
				Link link = makeLink(method);
				response.addLink(link);
			}
		}
		if (resource instanceof IdDiscoverableResource) {
			response.addLinks((List<Link>) ((IdDiscoverableResource)resource).discover());
		}
		return response;
	}

	public static Link makeLink(Method method) throws Exception {
		String uri = method.getName();
		String name = method.getName();
		String documentation = "";
		String httpMethod = findHttpMethod(method);
		Link link = new Link(uri, httpMethod, name, documentation);
		return link;
	}

	private static String findHttpMethod(Method method) throws Exception {
		String httpMethod = findHttpMethodFromAnnotation(method);
		if (httpMethod == null) {
			if (method.getReturnType().equals(Void.TYPE)) {
				httpMethod = HttpMethod.POST;
			} else {
				httpMethod = HttpMethod.GET;
			}
		}
		return httpMethod;
	}

	private static String findHttpMethodFromAnnotation(Method method) throws Exception {
		String httpMethod = doFindHttpMethodFromAnnotation(method);
		if (httpMethod != null) {
			return httpMethod;
		}
		final Class<?> declaringClass = method.getDeclaringClass();
		final Class<?>[] interfaces = declaringClass.getInterfaces();
		for (final Class<?> i : interfaces) {
			try {
				Method interfaceMethod = i.getMethod(method.getName(), method.getParameterTypes());
				httpMethod = findHttpMethodFromAnnotation(interfaceMethod);
				if (httpMethod != null) {
					return httpMethod;
				}
			} catch (NoSuchMethodException e) {
			}
		}
		return null;
	}

	private static String doFindHttpMethodFromAnnotation(Method method) {
		HttpMethod methodAnnotation = getAnnotation(method.getAnnotations(), HttpMethod.class);
		if (methodAnnotation != null) {
			return methodAnnotation.value();
		}
		return null;
	}

	private static <T extends Annotation> T getAnnotation(Annotation[] annotations, Class<T> clazz) {
		for (Annotation annotation : annotations) {
			T a = annotation.annotationType().getAnnotation(clazz);
			if (a != null) {
				return a;
			}
		}
		return null;
	}

}
