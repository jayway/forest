package com.jayway.forest.core;

import java.util.HashSet;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;

import com.jayway.forest.roles.Resource;

public class ForestApplication extends Application {
	
	private final Application delegate;
	private Set<Class<?>> delegateClasses;
	private Set<Object> delegateSingletons;
	
	public ForestApplication(Application delegate) {
		this.delegate = delegate;
		delegateClasses = getDelegateClasses();
		delegateSingletons = getDelegateSingletons();
	}
	
	@Override
	public Set<Class<?>> getClasses() {
		return delegateClasses;
	}

	@Override
	public Set<Object> getSingletons() {
		return delegateSingletons;
	}

	private Set<Object> getDelegateSingletons() {
		Set<Object> classes = delegate.getSingletons();
		Set<Object> result = new HashSet<Object>();
		for (Object object : classes) {
			result.add(proxy(object));
		}
		return result;
	}

	private Set<Class<?>> getDelegateClasses() {
		Set<Class<?>> classes = delegate.getClasses();
		Set<Class<?>> result = new HashSet<Class<?>>();
		for (Class<?> clazz : classes) {
			try {
				result.add(modify(clazz));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return result;
	}

	private Class<?> modify(Class<?> clazz) throws Exception {
		if (!Resource.class.isAssignableFrom(clazz)) {
			return clazz;
		}
		ClassPool pool = ClassPool.getDefault();
		CtClass sourceClass = pool.get(clazz.getName());
		CtClass targetClass = createSubclass(pool, sourceClass);
		copyPublicMethods(sourceClass, targetClass);
		prepareCommandQuery(targetClass);

		return targetClass.toClass();
	}

	// TODO: implement parameter annotation support
	private void prepareParameters(CtClass targetClass) throws Exception {
		for (CtMethod m : targetClass.getMethods()) {
			if (m.getParameterTypes().length > 0) {
				prepareParameterAnnotations(m);
			}
		}
	}

	private void copyPublicMethods(CtClass sourceClass, CtClass targetClass) throws CannotCompileException, NotFoundException {
		for (CtMethod sourceMethod : sourceClass.getMethods()) {
			if (shouldCopy(sourceMethod)) {
				CtMethod targetMethod = new CtMethod(sourceMethod, targetClass, null);
				AnnotationsAttribute info = (AnnotationsAttribute) sourceMethod.getMethodInfo().getAttribute(AnnotationsAttribute.visibleTag);
				if (info != null) {
					targetMethod.getMethodInfo().addAttribute(info.copy(targetClass.getClassFile().getConstPool(), null));
				}
				String returnStatement = "";
				if (!sourceMethod.getReturnType().equals(CtClass.voidType)) {
					returnStatement = "return ";
				}
				String body = "{"+ returnStatement +"super."+ sourceMethod.getName() +"($$);}";
				System.out.println(sourceMethod.getName() + " replacing body: " + body);
				targetMethod.setBody(body);
				targetClass.addMethod(targetMethod);
			}
		}
	}

	private CtClass createSubclass(ClassPool pool, CtClass sourceClass) throws CannotCompileException {
		CtClass targetClass = pool.makeClass(sourceClass.getName() + "2");
		AnnotationsAttribute sourceInfo = (AnnotationsAttribute) sourceClass.getClassFile().getAttribute(AnnotationsAttribute.visibleTag);
		if (sourceInfo != null) {
			targetClass.getClassFile().addAttribute(sourceInfo.copy(targetClass.getClassFile().getConstPool(), null));
		}
		targetClass.setSuperclass(sourceClass);
		return targetClass;
	}

	private boolean shouldCopy(CtMethod m) {
		return 	!m.getDeclaringClass().getName().equals("java.lang.Object") &&
				!Modifier.isFinal(m.getModifiers()) && 
				!Modifier.isAbstract(m.getModifiers()) && 
				!Modifier.isStatic(m.getModifiers()) && 
				!Modifier.isNative(m.getModifiers());
	}

	private void prepareCommandQuery(CtClass targetClass) throws Exception {
		for (CtMethod m : targetClass.getMethods()) {
			if (CtClass.voidType.equals(m.getReturnType())) {
				handleCommand(m);
			} else {
				handleQuery(m);
			}
		}
	}

	private void handleQuery(CtMethod m) throws Exception {
		addAnnotations(m, GET.class);
	}

	private void handleCommand(CtMethod m) throws Exception {
		addAnnotations(m, PUT.class);
	}

	private void addAnnotations(CtMethod m, Class<?> httpMethodAnnotation) throws Exception {
		ConstPool constPool = m.getDeclaringClass().getClassFile().getConstPool();
		AnnotationsAttribute info = (AnnotationsAttribute) m.getMethodInfo().getAttribute(AnnotationsAttribute.visibleTag);
		if (info == null) {
			info = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
		}
		if (!hasAnnotation(info.getAnnotations(), HttpMethod.class)) {
			Annotation get = new Annotation(httpMethodAnnotation.getName(), constPool);
			info.addAnnotation(get);
		}
		if (!m.hasAnnotation(Path.class)) {
			Annotation path = new Annotation(Path.class.getName(), constPool);
			path.addMemberValue("value", new StringMemberValue(m.getName(), constPool));
			info.addAnnotation(path);
		}
		m.getMethodInfo().addAttribute(info);
	}

	private void prepareParameterAnnotations(CtMethod m) {
		ConstPool constPool = m.getMethodInfo().getConstPool();
		ParameterAnnotationsAttribute info = new ParameterAnnotationsAttribute(constPool, ParameterAnnotationsAttribute.visibleTag);
		Annotation[][] annotations = new Annotation[1][1];
		Annotation param = new Annotation(FormParam.class.getName(), constPool);
		param.addMemberValue("value", new StringMemberValue("argument1", constPool));
		annotations[0][0] = param;
		info.setAnnotations(annotations);
		m.getMethodInfo().addAttribute(info);
		
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private boolean hasAnnotation(Annotation[] annotations, Class annotationClass) throws Exception {
		for (Annotation annotation : annotations) {
			if (Class.forName(annotation.getTypeName()).getAnnotation(annotationClass) != null) {
				return true;
			}
		}
		return false;
	}

	private Object proxy(Object object) {
		return object;
	}
}
