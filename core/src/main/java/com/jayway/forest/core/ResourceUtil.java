package com.jayway.forest.core;

import com.jayway.forest.constraint.Constraint;
import com.jayway.forest.constraint.ConstraintEvaluator;
import com.jayway.forest.constraint.Doc;
import com.jayway.forest.di.DependencyInjectionSPI;
import com.jayway.forest.reflection.Capability;
import com.jayway.forest.reflection.impl.*;
import com.jayway.forest.roles.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 */
public class ResourceUtil {

    static Logger log = LoggerFactory.getLogger(ResourceUtil.class);
    private final DependencyInjectionSPI dependencyInjectionSPI;

    public ResourceUtil(DependencyInjectionSPI dependencyInjectionSPI) {
        this.dependencyInjectionSPI = dependencyInjectionSPI;
    }

    public Object get( HttpServletRequest request, Resource resource, String get ) {
        return findCapability(resource, get).get(request);
    }

    public void put( Resource resource, String method, Map<String,String[]> formParams, InputStream stream, MediaTypeHandler mediaTypeHandler ) {
        findCapability(resource, method).put(formParams, stream, mediaTypeHandler );
    }

    public void post( Resource resource, String method, Map<String,String[]> formParams, InputStream stream, MediaTypeHandler mediaTypeHandler ) {
        findCapability(resource, method).post(formParams, stream, mediaTypeHandler );
    }

    public void delete(Resource resource, String delete) {
        findCapability(resource, delete).delete();
    }

    private String getDocumentation( Method method ) {
        for ( Annotation a : method.getAnnotations() ) {
            if ( a instanceof Doc) {
                return ((Doc) a).value();
            }
        }
        return null;
    }

    private boolean checkConstraint(Resource resource, Method method) {
        for ( Annotation a : method.getAnnotations() ) {
            if ( a.annotationType().getAnnotation(Constraint.class) != null ) {
                if ( !constraintEvaluator( resource, a ) ) return false;
            }
        }
        return true;
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

    public Resource invokePathMethod( Resource resource, String segment ) {
        return findCapability(resource, segment).subResource(segment);
    }

    private Capability findCapability(Resource resource, String name) {
        Class<? extends Resource> clazz = resource.getClass();
        if ( "create".equals( name ) && resource instanceof CreatableResource ) {
            try {
                for (Type type : clazz.getGenericInterfaces()) {
                    if ( type instanceof ParameterizedType && ((ParameterizedType) type).getRawType() == CreatableResource.class ) {
                        Type genericType = ((ParameterizedType) type).getActualTypeArguments()[0];
                        // no generic types supported in the Parameterized type of CreatableResource
                        Method method = clazz.getDeclaredMethod("create", (Class)genericType);
                        return createCapability(resource, method);
                    }
                }
            } catch (NoSuchMethodException e) {
                log.error( "Error finding Create method", e);
            }
        }
        if ( "update".equals( name ) && resource instanceof UpdatableResource ) {
            try {
                for (Type type : clazz.getGenericInterfaces()) {
                    if ( type instanceof ParameterizedType && ((ParameterizedType) type).getRawType() == UpdatableResource.class ) {
                        Type genericType = ((ParameterizedType) type).getActualTypeArguments()[0];
                        // no generic types supported in the Parameterized type of CreatableResource
                        Method method = clazz.getDeclaredMethod("update", (Class)genericType);
                        return createCapability(resource, method);
                    }
                }
            } catch (NoSuchMethodException e) {
                log.error( "Error finding Create method", e);
            }
        }
        for ( Method method : clazz.getDeclaredMethods() ) {
            if ( method.isSynthetic() ) continue;
            if ( method.getName().equals( name ) ) {
                return createCapability(resource, method);
            }
        }
        if (resource instanceof IdResource) {
            return new CapabilityIdResource((IdResource) resource, name, null);
        }
        return CapabilityNotFound.INSTANCE;
    }

    public Capability createCapability(Resource resource, Method method) {
        if (!Modifier.isAbstract(method.getModifiers()) && Modifier.isPublic(method.getModifiers())) {
            String name = method.getName();
            if (!checkConstraint(resource, method)) {
                return new CapabilityConstraintFailed(name);
            } else {
                String documentation = getDocumentation(method);
                if ( resource instanceof DeletableResource && method.getName().equals("delete") ) {
                    return new CapabilityDeleteCommand((DeletableResource) resource, documentation );
                } else if (resource instanceof UpdatableResource && method.getName().equals("update")) {
                    return new CapabilityUpdateCommand(method, (UpdatableResource) resource, documentation);
                } else if (method.getReturnType().equals(Void.TYPE)) {
                    return new CapabilityCommand(method, resource, documentation, method.getName() );
                } else if (resource instanceof CreatableResource && method.getName().equals( "create" )) {
                    return new CapabilityCreateCommand(method, (CreatableResource) resource, documentation );
                } else if (Resource.class.isAssignableFrom(method.getReturnType())) {
                    if (method.getParameterTypes().length == 0) return new CapabilitySubResource(resource, method, documentation);
                    else if (resource instanceof IdResource) return new CapabilityIdResource((IdResource) resource, name, documentation);
                } else if ( Iterable.class.isAssignableFrom( method.getReturnType() )) {
                    return new CapabilityQueryForList(dependencyInjectionSPI, resource, method, documentation );
                } else {
                    return new CapabilityQuery(resource, method, documentation, method.getName());
                }
            }
        }
        return CapabilityNotFound.INSTANCE;
    }
}