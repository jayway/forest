package com.jayway.forest.core;

import java.util.HashSet;
import java.util.Set;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;

import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;

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
		ClassPool pool = ClassPool.getDefault();
		CtClass targetClass = pool.getAndRename(clazz.getName(), clazz.getName() + "2");
		handlePublicMethods(targetClass);
		
		return targetClass.toClass();
	}

	private void handlePublicMethods(CtClass targetClass) throws Exception {
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
	/*
	private static Object getRoot() throws Exception {
		ClassPool pool = ClassPool.getDefault();
		CtClass rootResource = pool.getAndRename(RootResource.class.getName(), RootResource.class.getName() + "2");
		CtMethod simplePrint = CtNewMethod.make("public String simplePrint() { return \"qwe\"; }", rootResource);
		rootResource.addMethod(simplePrint); 
		ConstPool constPool = rootResource.getClassFile().getConstPool();
		AnnotationsAttribute info = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
		Annotation get = new Annotation(GET.class.getName(), constPool);
		info.addAnnotation(get);
		Annotation path = new Annotation(Path.class.getName(), constPool);
		path.addMemberValue("value", new StringMemberValue("simplePrint", constPool));
		info.addAnnotation(path);
		simplePrint.getMethodInfo().addAttribute(info);
		return rootResource.toClass().newInstance();
	}
	*/
}
