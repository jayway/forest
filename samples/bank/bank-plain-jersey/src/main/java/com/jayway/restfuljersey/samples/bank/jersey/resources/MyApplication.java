package com.jayway.restfuljersey.samples.bank.jersey.resources;

import java.util.Collections;
import java.util.Set;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;

public class MyApplication extends Application {
	
	private static Object root;
	
	static {
		try {
			root = getRoot();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Set<Object> getSingletons() {
		return Collections.singleton(root);
	}

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

//	@Path("simpleEcho")
//	@GET
//	public String simpleEcho(@QueryParam("param") String value) {
//		return value;
//	}

}
