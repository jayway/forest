package com.jayway.forest.legacy.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.forest.legacy.reflection.impl.Parameter;
import com.jayway.forest.legacy.roles.Resource;
import com.jayway.forest.legacy.roles.Template;

public abstract class ReflectionUtil {
	protected static Logger log = LoggerFactory.getLogger(ReflectionUtil.class);
    private ReflectionUtil() {}

	public static Set<Class<?>> basicTypes;

	static {
		Set<Class<?>> basicTypes = new HashSet<Class<?>>();
        basicTypes.add( String.class);
        basicTypes.add( Long.class );
        basicTypes.add( Integer.class);
        basicTypes.add( Double.class );
        basicTypes.add( Float.class );
        basicTypes.add( Boolean.class );
        ReflectionUtil.basicTypes = Collections.unmodifiableSet(basicTypes);
    }


    public static List<Parameter> parameterList(Method method, Resource resource) {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        List<Parameter> parameters = new ArrayList<Parameter>();
        for ( int i =0; i< method.getParameterTypes().length; i++) {
            Class<?> aClass = method.getParameterTypes()[i];
            Parameter parameter = new Parameter(i, aClass );
            parameters.add( parameter );

            for (Annotation annotation : parameterAnnotations[i]) {
                if ( annotation instanceof FormParam) {
                	parameter.setName(((FormParam)annotation).value());
                } else if ( annotation instanceof QueryParam) {
                	parameter.setName(((QueryParam)annotation).value());
                } else if ( annotation instanceof Template) {
                    String methodName = ((Template) annotation).value();
                    Method template;
                    try {
                    	template = resource.getClass().getDeclaredMethod(methodName);
                    } catch (NoSuchMethodException e) {
                    	throw new IllegalArgumentException(String.format("Template method [%s] does not exist for resource [%s]!", methodName, resource.getClass().getName()));
                    }
                    try {
                        if ( aClass.isAssignableFrom( template.getReturnType() ) ) {
                            template.setAccessible( true );
                            parameter.setTemplate( template.invoke(resource) );
                        }
                    } catch (InvocationTargetException e) {
                    	throw new RuntimeException(String.format("Could not evaluate template method [%s]. Template methods must have zero arguments and return type must match argument type", method.getName()), e.getTargetException());
                    } catch (Throwable e) {
                    	throw new RuntimeException(String.format("Could not evaluate template method [%s]. Template methods must have zero arguments and return type must match argument type", method.getName()), e);
                    }
                }
            }
        }
        return parameters;
    }
}
