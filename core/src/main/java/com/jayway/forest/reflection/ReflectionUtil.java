package com.jayway.forest.reflection;

import com.jayway.forest.reflection.impl.Parameter;
import com.jayway.forest.roles.Resource;
import com.jayway.forest.roles.Template;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public abstract class ReflectionUtil {
	private ReflectionUtil() {}

	public static Set<Class<?>> basicTypes;

	static {
		Set<Class<?>> basicTypes = new HashSet<Class<?>>();
        basicTypes.add( String.class);
        basicTypes.add( Long.class );
        basicTypes.add( Integer.class);
        basicTypes.add( Double.class );
        basicTypes.add( Boolean.class );
        ReflectionUtil.basicTypes = Collections.unmodifiableSet(basicTypes);
    }


    public static List<Parameter> parameterList(Method method, Resource resource) {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        List<Parameter> parameters = new ArrayList<Parameter>();
        for ( int i =0; i< method.getParameterTypes().length; i++) {
            Class<?> aClass = method.getParameterTypes()[i];
            Parameter parameter = new Parameter( aClass );
            parameters.add( parameter );

            for (Annotation annotation : parameterAnnotations[i]) {
                if ( annotation instanceof Template) {
                    String methodName = ((Template) annotation).value();
                    try {
                        Method template = resource.getClass().getDeclaredMethod(methodName);
                        if ( aClass.isAssignableFrom( template.getReturnType() ) && Modifier.isPrivate(template.getModifiers()) ) {
                            template.setAccessible( true );
                            parameter.setTemplate( template.invoke(resource) );
                        }
                    } catch (Throwable e) {
                        // ignore. parameter template will be null
                    }
                }
            }
        }
        return parameters;
    }
}
