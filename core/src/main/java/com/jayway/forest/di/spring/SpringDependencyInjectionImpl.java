package com.jayway.forest.di.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.jayway.forest.di.DependencyInjectionSPI;

public class SpringDependencyInjectionImpl implements DependencyInjectionSPI, ApplicationContextAware {

	private ApplicationContext applicationContext;

	@Override
	public <T> void addRequestContext(Class<T> clazz, T object) {
		RequestContextHolder.getRequestAttributes().setAttribute(clazz.getName(), object, RequestAttributes.SCOPE_REQUEST);
	}

	@Override
	public <T> T postCreate(T object) {
		applicationContext.getAutowireCapableBeanFactory().autowireBean(object);
		return object;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
