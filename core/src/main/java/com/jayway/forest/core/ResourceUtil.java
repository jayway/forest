package com.jayway.forest.core;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.jayway.forest.reflection.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.forest.constraint.Constraint;
import com.jayway.forest.constraint.ConstraintEvaluator;
import com.jayway.forest.constraint.Doc;
import com.jayway.forest.di.DependencyInjectionSPI;
import com.jayway.forest.reflection.CommandCapability;
import com.jayway.forest.reflection.CapabilityNotAllowed;
import com.jayway.forest.reflection.IdCapability;
import com.jayway.forest.reflection.CapabilityNotFound;
import com.jayway.forest.reflection.QueryCapability;
import com.jayway.forest.reflection.Capability;
import com.jayway.forest.reflection.SubResource;
import com.jayway.forest.roles.IdResource;
import com.jayway.forest.roles.Resource;

/**
 */
public class ResourceUtil {

    static Logger log = LoggerFactory.getLogger(ResourceUtil.class);
	private final DependencyInjectionSPI dependencyInjectionSPI;

    public ResourceUtil(DependencyInjectionSPI dependencyInjectionSPI) {
		this.dependencyInjectionSPI = dependencyInjectionSPI;
	}

    public boolean checkConstraint(Resource resource, Method method) {
        for ( Annotation a : method.getAnnotations() ) {
            if ( a.annotationType().getAnnotation(Constraint.class) != null ) {
                if ( !constraintEvaluator( resource, a ) ) return false;
            }
        }
        return true;
    }

    public String getDocumentation( Method method ) {
        for ( Annotation a : method.getAnnotations() ) {
            if ( a instanceof Doc) {
                return ((Doc) a).value();
            }
        }
        return null;
    }

    private boolean constraintEvaluator( Resource resource, Annotation annotation ) {
        if ( annotation == null ) return true;
        Constraint constraint = annotation.annotationType().getAnnotation(Constraint.class);
        try {
            @SuppressWarnings("unchecked")
			ConstraintEvaluator<Annotation, Resource> constraintEvaluator = constraint.value().newInstance();
            constraintEvaluator = dependencyInjectionSPI.postCreate(constraintEvaluator);
            return constraintEvaluator.isValid( annotation, resource);

        } catch (InstantiationException e) {
            log.error("Could not instantiate constraint", e);
        } catch (IllegalAccessException e) {
            log.error( "Constraint need public empty argument constructor", e);
        }
        return true;
    }

    public void post( Resource resource, String method, Map<String,String[]> formParams, InputStream stream, MediaTypeHandler mediaTypeHandler) {
        findMethod( resource, method ).post(formParams, stream, mediaTypeHandler);
    }

    public Resource invokePathMethod( Resource resource, String path ) {
        return findMethod( resource, path).subResource(path);
    }

    Capability findMethod( Resource resource, String name ) {
        Class<? extends Resource> clazz = resource.getClass();
        for ( Method method : clazz.getDeclaredMethods() ) {
            if ( method.isSynthetic() ) continue;
            if ( method.getName().equals( name ) ) {
                return makeResourceMethod( resource, method );
            }
        }
        if (resource instanceof IdResource) {
        	// TODO: find documentation for IdResource
        	return new IdCapability((IdResource) resource, name, null);
        }
        return CapabilityNotFound.INSTANCE;
    }
    
    public Capability makeResourceMethod(Resource resource, Method method) {
        if (!Modifier.isAbstract(method.getModifiers()) && Modifier.isPublic(method.getModifiers())) {
            String name = method.getName();
			if (!checkConstraint(resource, method)) {
                return new CapabilityNotAllowed(name);
            } else {
                String documentation = getDocumentation(method);
                if (method.getReturnType().equals(Void.TYPE)) {
                    return new CommandCapability(method, resource, name, documentation);
                } else if (Resource.class.isAssignableFrom(method.getReturnType())) {
                    if (method.getParameterTypes().length == 0) return new SubResource(resource, method, name, documentation);
                    else if (resource instanceof IdResource) return new IdCapability((IdResource) resource, name, documentation);
                } else if ( List.class.isAssignableFrom( method.getReturnType() )) {
                    return new QueryForListCapability(dependencyInjectionSPI, resource, method, name, documentation );
                } else {
                    return new QueryCapability(resource, method, name, documentation);
                }
            }
        }
        return CapabilityNotFound.INSTANCE;
    }

    public void invokeDelete( Resource resource ) {
    	findMethod( resource, "delete").delete();
    }

    public Object get( HttpServletRequest request, Resource resource, String get ) {
        return findMethod(resource, get).get(request);
    }
}
