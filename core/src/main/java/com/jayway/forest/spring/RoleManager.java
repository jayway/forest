package com.jayway.forest.spring;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public class RoleManager {

    @SuppressWarnings("unchecked")
	public static <T> T role(Class<T> clazz) {
    	return (T) RequestContextHolder.getRequestAttributes().getAttribute(clazz.getName(), RequestAttributes.SCOPE_REQUEST);
    }

    public static <T> void addRole(Class<T> clazz, Object object) {
        RequestContextHolder.getRequestAttributes().setAttribute(clazz.getName(), object, RequestAttributes.SCOPE_REQUEST);
    }

    public static void clear() {
        RequestContextHolder.resetRequestAttributes();
    }
}
