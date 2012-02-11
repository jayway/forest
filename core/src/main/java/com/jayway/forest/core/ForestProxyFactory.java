package com.jayway.forest.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtField.Initializer;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.ConstPool;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.jayway.forest.Body;
import com.jayway.forest.constraint.Constraint;
import com.jayway.forest.constraint.ConstraintHandler;
import com.jayway.forest.constraint.ConstraintViolationException;
import com.jayway.forest.core.javassist.AnnotationUtil;
import com.jayway.forest.hypermedia.HyperMediaResponse;
import com.jayway.forest.hypermedia.HyperMediaResponseFactory;
import com.jayway.forest.hypermedia.RequestDescriptionFactory;
import com.jayway.forest.roles.ReadableResource;
import com.jayway.forest.roles.Resource;

public class ForestProxyFactory {
	private static final Class<?> DEFAULT_HTTP_METHOD_FOR_QUERY = GET.class;
	private static final Class<?> DEFAULT_HTTP_METHOD_FOR_COMMAND = POST.class;

	public static final String FOREST_GET_HYPERMEDIA = "forest_getHypermedia";

	private final ClassPool pool = new ClassPool(true);
	
	public ForestProxyFactory() {
		pool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
	}

	public Object proxy(Object object) throws Exception {
		Class<?> clazz = getProxyClass(object.getClass());
		Constructor<?> constructor = clazz.getConstructor(object.getClass());
		return constructor.newInstance(object);
	}

	public Class<?> getProxyClass(Class<?> clazz) throws Exception {
		try {
			return Class.forName(getProxyName(clazz.getName()));
		} catch (ClassNotFoundException e) {
			return createProxy(clazz);
		}
	}
	
	private Class<?> createProxy(Class<?> clazz) throws Exception {
		if (!Resource.class.isAssignableFrom(clazz)) {
			return clazz;
		}
		CtClass sourceClass = pool.get(clazz.getName());
		CtClass targetClass = createProxyClass(pool, sourceClass);
		copyPublicMethods(sourceClass, targetClass);
		addMethodForHypermedia(targetClass, clazz);

		return targetClass.toClass();
	}

	private void addMethodForHypermedia(CtClass targetClass, Class<?> sourceClass) throws Exception {
		CtClass ctHypermediaResponse = pool.get(HyperMediaResponse.class.getName());
		CtMethod method = new CtMethod(ctHypermediaResponse, FOREST_GET_HYPERMEDIA, null, targetClass);
		// TODO: could we cache the factory?
		String body = "null";
		String bodyClassName = "java.lang.String";
		if (ReadableResource.class.isAssignableFrom(sourceClass)) {
			body = "delegate.read()";
			bodyClassName = ((Class<?>)findActualTypeArguments(sourceClass, ReadableResource.class)[0]).getName();
		}
		method.setBody(String.format("return %s.create(delegate.getClass()).make(delegate, %s, %s.class);", HyperMediaResponseFactory.class.getName(), body, bodyClassName));

		ConstPool constPool = targetClass.getClassFile().getConstPool();
		AnnotationsAttribute attribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
		attribute.addAnnotation(new Annotation(constPool, pool.get(GET.class.getName())));
		Annotation pathAnnotation = new Annotation(constPool, pool.get(Path.class.getName()));
		pathAnnotation.addMemberValue("value", new StringMemberValue("", constPool));
		attribute.addAnnotation(pathAnnotation);
		method.getMethodInfo().addAttribute(attribute);

		targetClass.addMethod(method);
	}

	private Type[] findActualTypeArguments(Class<?> sourceClass, Class<?> interfaceClass) {
		Type[] genericInterfaces = sourceClass.getGenericInterfaces();
		for (Type type : genericInterfaces) {
			if (type instanceof ParameterizedType) {
				ParameterizedType parameterizedType = (ParameterizedType)type;
				if (parameterizedType.getRawType().equals(interfaceClass)) {
					return parameterizedType.getActualTypeArguments();
				}
			}
		}
		throw new IllegalArgumentException(String.format("For %s, could not find actual types of %s", sourceClass.getName(), interfaceClass.getName()));
	}

	private void copyPublicMethods(CtClass sourceClass, CtClass targetClass) throws Exception {
		for (CtMethod sourceMethod : sourceClass.getMethods()) {
			if (shouldCopy(sourceMethod)) {
				boolean isCommand = sourceMethod.getReturnType().equals(CtClass.voidType);
				CtMethod targetMethod = copyMethod(sourceClass, targetClass, sourceMethod, isCommand);
				
				if (isCommand || targetMethod.getParameterTypes().length > 0) {
					addDescriptionMethod(targetClass, sourceMethod, targetMethod, isCommand);
				}
			}
		}
	}

	private void addDescriptionMethod(CtClass targetClass, CtMethod sourceMethod, CtMethod targetMethod, boolean isCommand) throws Exception {
		CtClass ctReturnType = pool.get(Response.class.getName());
		CtMethod descriptionMethod = new CtMethod(ctReturnType, sourceMethod.getName() + "_description", null, targetClass);
		ConstPool constPool = targetClass.getClassFile().getConstPool();
		AnnotationsAttribute attribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
		attribute.addAnnotation(new Annotation(constPool, pool.get(DEFAULT_HTTP_METHOD_FOR_QUERY.getName())));
		AnnotationsAttribute targetAttribute = (AnnotationsAttribute) targetMethod.getMethodInfo().getAttribute(AnnotationsAttribute.visibleTag);
		attribute.addAnnotation(targetAttribute.getAnnotation(Path.class.getName()));
		// TODO: how to handle description method for queries?!
		if (isCommand) {
			descriptionMethod.getMethodInfo().addAttribute(attribute);
		}
		
		StringBuilder body = new StringBuilder("{");
		body.append(String.format("return %s.status(405).entity(%s.create(%s.class).make(delegate, \"%s\")).build();", Response.class.getName(), RequestDescriptionFactory.class.getName(), sourceMethod.getDeclaringClass().getName(), sourceMethod.getName()));
		body.append("}");
		descriptionMethod.setBody(body.toString());

		targetClass.addMethod(descriptionMethod);
	}

	private CtMethod copyMethod(CtClass sourceClass, CtClass targetClass, CtMethod sourceMethod, boolean isCommand) throws Exception {
		CtMethod targetMethod;
		if (isCommand) {
			targetMethod = createCommand(targetClass, sourceMethod);
		} else {
			targetMethod = createQuery(targetClass, sourceMethod);
		}
		StringBuilder body = new StringBuilder("{");
		AnnotationsAttribute info = (AnnotationsAttribute) sourceMethod.getMethodInfo().getAttribute(AnnotationsAttribute.visibleTag);
		if (info != null && AnnotationUtil.hasAnnotation(info.getAnnotations(), Constraint.class)) {
			String methodFieldName = addFieldForMethodReflection(sourceClass, targetClass, sourceMethod, targetMethod);
			body.append(String.format("if (%s.constrained(delegate, %s)) throw new %s();", ConstraintHandler.class.getName(), methodFieldName, ConstraintViolationException.class.getName()));
		}
		if (!isCommand) {
			body.append("return ");
		}
		body.append("delegate."+ sourceMethod.getName() +"($$); ");
		if (isCommand) {
			body.append("return \"Operation completed successfully\";");
		}
		body.append("}");
		targetMethod.setBody(body.toString());
		targetClass.addMethod(targetMethod);
		return targetMethod;
	}

	private String addFieldForMethodReflection(CtClass sourceClass, CtClass targetClass, CtMethod sourceMethod, CtMethod targetMethod) throws NotFoundException,
			CannotCompileException {
		String methodFieldName = targetMethod.getName() + "Method";
		try {
			targetClass.getField(methodFieldName);
			return methodFieldName;
		} catch (NotFoundException e) {
			// ignore
		}
		CtClass ctMethod = this.pool.get(Method.class.getName());
		CtClass ctProxyHelper = this.pool.get(ProxyHelper.class.getName());
		CtField field = new CtField(ctMethod, methodFieldName, targetClass);
		field.setModifiers(Modifier.STATIC);
		targetClass.addField(field, Initializer.byCallWithParams(ctProxyHelper, "getMethodObject", new String[] {sourceClass.getName(), sourceMethod.getName()}));
		return methodFieldName;
	}

	private CtMethod createQuery(CtClass targetClass, CtMethod sourceMethod) throws CannotCompileException, Exception {
		CtMethod targetMethod;
		targetMethod = new CtMethod(sourceMethod, targetClass, null);
		copyAttributeInfo(sourceMethod, targetMethod, AnnotationsAttribute.visibleTag);
		copyAttributeInfo(sourceMethod, targetMethod, ParameterAnnotationsAttribute.visibleTag);
		addAnnotations(sourceMethod.getName(), targetMethod, DEFAULT_HTTP_METHOD_FOR_QUERY);
		prepareParameterAnnotations(targetMethod, QueryParam.class.getName());
		return targetMethod;
	}

	private CtMethod createCommand(CtClass targetClass, CtMethod sourceMethod) throws NotFoundException, Exception {
		CtMethod targetMethod;
		CtClass returnType = targetClass.getClassPool().get("java.lang.String");
		targetMethod = new CtMethod(returnType, sourceMethod.getName() + "_proxy", sourceMethod.getParameterTypes(), targetClass);
		copyAttributeInfo(sourceMethod, targetMethod, AnnotationsAttribute.visibleTag);
		copyAttributeInfo(sourceMethod, targetMethod, ParameterAnnotationsAttribute.visibleTag);
		addAnnotations(sourceMethod.getName(), targetMethod, DEFAULT_HTTP_METHOD_FOR_COMMAND);
		if (targetMethod.getParameterTypes().length > 1 || (targetMethod.getParameterTypes().length == 1 && isSimpleType(targetMethod.getParameterTypes()[0]) && !hasBodyAnnotation(targetMethod))) {
			prepareParameterAnnotations(targetMethod, FormParam.class.getName());
		}
		return targetMethod;
	}

	private boolean hasBodyAnnotation(CtMethod targetMethod) throws Exception {
		Object[] objects = targetMethod.getParameterAnnotations()[0];
		for (Object object : objects) {
			if (object instanceof Body) {
				return true;
			}
		}
		return false;
	}

	private void copyAttributeInfo(CtMethod sourceMethod, CtMethod targetMethod, String name) {
		AttributeInfo info = sourceMethod.getMethodInfo().getAttribute(name);
		if (info != null) {
			targetMethod.getMethodInfo().addAttribute(info.copy(targetMethod.getDeclaringClass().getClassFile().getConstPool(), null));
		}
	}

	private CtClass createProxyClass(ClassPool pool, CtClass sourceClass) throws Exception {
		CtClass targetClass = pool.makeClass(getProxyName(sourceClass.getName()));
		copyAnnotations(sourceClass, targetClass);
		createConstructors(sourceClass, targetClass);
		return targetClass;
	}

	private void createConstructors(CtClass sourceClass, CtClass targetClass) throws Exception {
		targetClass.addField(new CtField(sourceClass, "delegate", targetClass));
		
		if (hasNoArgConstructor(sourceClass)) {
			CtConstructor noArgConstructor = new CtConstructor(new CtClass[] {}, targetClass);
			noArgConstructor.setBody(String.format("delegate = new %s();", sourceClass.getName()));
			targetClass.addConstructor(noArgConstructor);
		}

		CtConstructor delegateConstructor = new CtConstructor(new CtClass[] {sourceClass}, targetClass);
		delegateConstructor.setBody("delegate = $1;");
		targetClass.addConstructor(delegateConstructor);
}

	private boolean hasNoArgConstructor(CtClass sourceClass) throws NotFoundException {
		CtConstructor[] constructors = sourceClass.getConstructors();
		for (CtConstructor ctConstructor : constructors) {
			if (ctConstructor.getParameterTypes().length == 0) {
				return true;
			}
		}
		return false;
	}

	private void copyAnnotations(CtClass sourceClass, CtClass targetClass) {
		AnnotationsAttribute sourceInfo = (AnnotationsAttribute) sourceClass.getClassFile().getAttribute(AnnotationsAttribute.visibleTag);
		if (sourceInfo != null) {
			targetClass.getClassFile().addAttribute(sourceInfo.copy(targetClass.getClassFile().getConstPool(), null));
		}
	}

	private String getProxyName(String sourceClassName) {
		return sourceClassName + "_Proxy";
	}

	private boolean shouldCopy(CtMethod m) {
		return 	!m.getDeclaringClass().getName().equals("java.lang.Object") &&
				!Modifier.isFinal(m.getModifiers()) && 
				!Modifier.isAbstract(m.getModifiers()) && 
				!Modifier.isStatic(m.getModifiers()) && 
				!Modifier.isNative(m.getModifiers());
	}

	private boolean isSimpleType(CtClass clazz) {
		if (clazz.isPrimitive() || clazz.getName().equals("java.lang.String")) {
			return true;
		}
		return false;
	}

	private void addAnnotations(String pathValue, CtMethod m, Class<?> httpMethodAnnotation) throws Exception {
		ConstPool constPool = m.getDeclaringClass().getClassFile().getConstPool();
		AnnotationsAttribute info = (AnnotationsAttribute) m.getMethodInfo().getAttribute(AnnotationsAttribute.visibleTag);
		if (info == null) {
			info = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
		}
		if (!AnnotationUtil.hasAnnotation(info.getAnnotations(), HttpMethod.class)) {
			Annotation get = new Annotation(httpMethodAnnotation.getName(), constPool);
			info.addAnnotation(get);
		}
		if (!m.hasAnnotation(Path.class)) {
			Annotation path = new Annotation(Path.class.getName(), constPool);
			path.addMemberValue("value", new StringMemberValue(pathValue, constPool));
			info.addAnnotation(path);
		}
		m.getMethodInfo().addAttribute(info);
	}

	private void prepareParameterAnnotations(CtMethod m, String annotationClassName) throws Exception {
		if (m.getParameterTypes().length == 0) {
			return;
		}
		ConstPool constPool = m.getMethodInfo().getConstPool();
		int parameterCount = m.getParameterTypes().length;
		ParameterAnnotationsAttribute info = (ParameterAnnotationsAttribute) m.getMethodInfo().getAttribute(ParameterAnnotationsAttribute.visibleTag);
		Annotation[][] annotations;
		if (info == null) {
			info = new ParameterAnnotationsAttribute(constPool, ParameterAnnotationsAttribute.visibleTag);
			annotations = new Annotation[parameterCount][];
		} else {
			annotations = info.getAnnotations();
		}
		for (int indx=0; indx<parameterCount; indx++) {
			List<Annotation> paramAnnotations;
			if (annotations[indx] == null) {
				paramAnnotations = new LinkedList<Annotation>();
			} else {
				paramAnnotations = new ArrayList<Annotation>(Arrays.asList(annotations[indx]));
			}
			if (!AnnotationUtil.contains(paramAnnotations, annotationClassName)) { 
				Annotation param = new Annotation(annotationClassName, constPool);
				param.addMemberValue("value", new StringMemberValue("argument" + (indx+1), constPool));
				paramAnnotations.add(param);
			}
			annotations[indx] = paramAnnotations.toArray(new Annotation[paramAnnotations.size()]);
		}
		info.setAnnotations(annotations);
		m.getMethodInfo().addAttribute(info);
	}
}
