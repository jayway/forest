package com.jayway.forest.core.javassist;

import java.lang.reflect.Method;

import javassist.CtClass;
import javassist.CtMethod;

@SuppressWarnings({"rawtypes","unchecked"})
public abstract class JavassistHelper {
	private JavassistHelper() {}
	
	public static Class toReflection(CtClass clazz) throws Exception {
		return Class.forName(clazz.getName());
	}
	public static Class[] toReflection(CtClass[] classes) throws Exception {
		Class[] result = new Class[classes.length];
		for (int indx=0; indx<result.length; indx++) {
			result[indx] = toReflection(classes[indx]);
		}
		return result;
	}
	public static Method toReflection(CtMethod method) throws Exception {
		Class clazz = toReflection(method.getDeclaringClass());
		return clazz.getMethod(method.getName(), toReflection(method.getParameterTypes()));
	}
}
