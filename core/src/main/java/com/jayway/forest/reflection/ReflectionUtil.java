package com.jayway.forest.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.forest.reflection.impl.Parameter;
import com.jayway.forest.roles.Resource;
import com.jayway.forest.roles.Template;

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
            Parameter parameter = new Parameter( aClass );
            parameters.add( parameter );

            for (Annotation annotation : parameterAnnotations[i]) {
                if ( annotation instanceof Template) {
                    String methodName = ((Template) annotation).value();
                    try {
                        Method template = resource.getClass().getDeclaredMethod(methodName);
                        if ( aClass.isAssignableFrom( template.getReturnType() ) ) {
                            template.setAccessible( true );
                            parameter.setTemplate( template.invoke(resource) );
                        }
                    } catch (NoSuchMethodException e) {
                    	throw new IllegalArgumentException(String.format("Template method [%s] does not exist for resource [%s]!", methodName, resource.getClass().getName()));
                    } catch (InvocationTargetException e) {
                    	throw new RuntimeException("Could not evaluate template method. Template methods must have zero arguments and return type must match argument type", e.getTargetException());
                    } catch (Throwable e) {
                    	throw new RuntimeException("Could not evaluate template method. Template methods must have zero arguments and return type must match argument type", e);
                    }
                }
            }
        }
        return parameters;
    }
}
