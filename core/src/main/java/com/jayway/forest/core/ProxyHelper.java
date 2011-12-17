package com.jayway.forest.core;

import java.lang.reflect.Method;
import java.util.Arrays;

public final class ProxyHelper {
	private ProxyHelper() {}
	
	public static Method getMethodObject(String[] args) {
		if (args.length != 2) {
			throw new IllegalArgumentException("Not valid: " + Arrays.asList(args));
		}
		String className = args[0];
		String methodName = args[1];
		try {
			for (Method method : Class.forName(className).getMethods()) {
				if (method.getName().equals(methodName)) {
					return method;
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		throw new IllegalArgumentException(String.format("failed to find method %s.%s", className, methodName));
	}
}
