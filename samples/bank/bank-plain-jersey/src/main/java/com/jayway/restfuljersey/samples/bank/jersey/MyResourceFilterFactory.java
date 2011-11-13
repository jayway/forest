package com.jayway.restfuljersey.samples.bank.jersey;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.Context;

import com.jayway.forest.constraint.Constraint;
import com.sun.jersey.api.core.ExtendedUriInfo;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;

public class MyResourceFilterFactory implements ResourceFilterFactory {
	
	private final ExtendedUriInfo context;
	
	public MyResourceFilterFactory(@Context ExtendedUriInfo context) {
		this.context = context;
		
	}

	@Override
	public List<ResourceFilter> create(AbstractMethod abstractMethod) {
		if (hasConstraint(abstractMethod)) {
			return Collections.<ResourceFilter>singletonList(new MyResourceFilter(abstractMethod, context));
		}
		return Collections.emptyList();
	}
	
	private boolean hasConstraint(AbstractMethod abstractMethod) {
		for (Annotation a : abstractMethod.getAnnotations()) {
            if ( a.annotationType().getAnnotation(Constraint.class) != null ) {
            	return true;
            }
		}
		return false;
	}

	private static class MyResourceFilter implements ResourceFilter, ContainerRequestFilter {

		private final AbstractMethod abstractMethod;
		private final ExtendedUriInfo context;

		public MyResourceFilter(AbstractMethod abstractMethod, ExtendedUriInfo context) {
			this.abstractMethod = abstractMethod;
			this.context = context;
		}

		@Override
		public ContainerRequestFilter getRequestFilter() {
			return this;
//			AbstractResource resource = abstractMethod.getResource();
//			throw new RuntimeException("STOP");
//			return null;
		}

		@Override
		public ContainerResponseFilter getResponseFilter() {
			return null;
		}

		@Override
		public ContainerRequest filter(ContainerRequest request) {
			System.out.println("HELLO from " + context.getMatchedResources().get(0));
			return request;
		}
		
	}
}
