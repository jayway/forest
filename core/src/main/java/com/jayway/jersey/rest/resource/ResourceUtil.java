package com.jayway.jersey.rest.resource;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.forest.DependencyInjectionSPI;
import com.jayway.jersey.rest.RestfulServlet;
import com.jayway.jersey.rest.constraint.Constraint;
import com.jayway.jersey.rest.constraint.ConstraintEvaluator;
import com.jayway.jersey.rest.constraint.Doc;
import com.jayway.jersey.rest.exceptions.BadRequestException;
import com.jayway.jersey.rest.exceptions.InternalServerErrorException;
import com.jayway.jersey.rest.exceptions.MethodNotAllowedException;
import com.jayway.jersey.rest.exceptions.MethodNotAllowedRenderTemplateException;
import com.jayway.jersey.rest.exceptions.NotFoundException;
import com.jayway.jersey.rest.exceptions.UnsupportedMediaTypeException;
import com.jayway.jersey.rest.roles.DeletableResource;
import com.jayway.jersey.rest.roles.IdResource;

/**
 */
public class ResourceUtil {

    static Logger log = LoggerFactory.getLogger(ResourceUtil.class);
	private final DependencyInjectionSPI dependencyInjectionSPI;

    public ResourceUtil(DependencyInjectionSPI dependencyInjectionSPI) {
		this.dependencyInjectionSPI = dependencyInjectionSPI;
	}

	boolean checkConstraint(Resource resource, Method method) {
        for ( Annotation a : method.getAnnotations() ) {
            if ( a.annotationType().getAnnotation(Constraint.class) != null ) {
                if ( !constraintEvaluator( resource, a ) ) return false;
            }
        }
        return true;
    }

    String getDocumentation( Method method ) {
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
        ResourceMethod m = findMethod( resource, method );
        if ( !m.isCommand() || m.isNotFound() || m.isConstraintFalse() || m.isIdSubResource() ) throw notFound();

        Object[] arguments = stream == null ? arguments(m.method(), formParams ) : arguments( m.method(), stream, mediaTypeHandler );
        invokeCommand(m.method(), resource, arguments );
    }


    private Object[] arguments( Method m, InputStream stream, MediaTypeHandler mediaTypeHandler ) {
        if ( mediaTypeHandler.contentTypeJSON() ) {
            return new JSONHelper().handleArguments( m, stream );
        }
        // TODO support other types
        throw new UnsupportedMediaTypeException();
    }

    private Object[] arguments( Method m, Map<String, String[]> formParams ) {
        if ( m.getParameterTypes().length == 0 ) {
            return new Object[0];
        }
        Object[] args = new Object[m.getParameterTypes().length];

        for ( int i=0; i<args.length; i++ ) {
            Class<?> type = m.getParameterTypes()[i];
            args[i] = mapArguments( type, formParams, "argument"+(i+1) );
        }
        return args;
    }

    public Resource invokePathMethod( Resource resource, String path ) {
        ResourceMethod method = findMethod( resource, path);
        if ( method.isSubResource() ) {
            try {
                return (Resource) method.method().invoke( resource );
            } catch ( IllegalAccessException e) {
                e.printStackTrace();
                throw internalServerError( e );
            } catch ( InvocationTargetException e) {
                if ( e.getCause() instanceof RuntimeException ) {
                    throw (RuntimeException) e.getCause();
                }
                log.error("Error invoking resource method", e);
                throw internalServerError( e );
            }
        } else if ( ( method.isIdSubResource() || method.isNotFound() ) && resource instanceof IdResource) {
            ResourceMethod id = findMethod( resource, "id");
            if ( id.isConstraintFalse() ) throw notFound();
            return ((IdResource) resource).id( path );
        }
        throw notFound();
    }

    ResourceMethod findMethod( Resource resource, String name ) {
        Class<? extends Resource> clazz = resource.getClass();
        for ( Method method : clazz.getDeclaredMethods() ) {
            if ( method.isSynthetic() ) continue;
            if ( method.getName().equals( name ) ) {
                return new ResourceMethod( this, resource, method );
            }
        }
        return new ResourceMethod();
    }

    public void invokeDelete( Resource resource ) {
        if ( resource instanceof DeletableResource) {
            ResourceMethod delete = findMethod( resource, "delete");
            if ( delete.isConstraintFalse() ) throw notFound();
            ((DeletableResource) resource).delete();
        } else {
            throw new MethodNotAllowedException();
        }
    }

    public <T extends Resource> void invokeCommand(Method method, T instance, Object... arguments) {
        try {
            method.invoke( instance, arguments );
        } catch (InvocationTargetException e) {
            if ( e.getCause() instanceof RuntimeException ) {
                log.error( e.getCause().getMessage(), e);
                throw (RuntimeException) e.getCause();
            }
            throw badRequest( e );
        } catch (IllegalAccessException e) {
            log.error("Could not access command", e);
            throw internalServerError( e );
        }
    }

    public Object get( HttpServletRequest request, Resource resource, String get ) {
        ResourceMethod m = findMethod(resource, get);
        if ( m.isSubResource() || m.isNotFound() || m.isConstraintFalse() || m.isIdSubResource() ) throw notFound();

        if ( m.isCommand() ) {
            throw new MethodNotAllowedRenderTemplateException( m );
        }

        Map<String, String[]> queryParams = request.getParameterMap();
        if ( queryParams.size() == 0 && m.method().getParameterTypes().length > 0) {
            throw new MethodNotAllowedRenderTemplateException( m );
        } else {
            try {
                if ( m.method().getParameterTypes().length == 0 ) {
                    return m.method().invoke( resource );
                }
                Object[] args = new Object[m.method().getParameterTypes().length];
                for ( int i=0; i<args.length; i++) {
                    Class<?> type = m.method().getParameterTypes()[i];
                    args[i] = mapArguments( type, queryParams, "argument"+(i+1));
                }
                return m.method().invoke( resource, args);
            } catch ( IllegalAccessException e) {
                throw internalServerError( e );
            } catch ( InvocationTargetException e) {
                throw internalServerError( e );
            }
        }
    }

    private Object mapArguments( Class<?> dto, Map<String,String[]> formParams, String prefix ) {
        try {
            if ( RestfulServlet.basicTypes.contains( dto ) ) {
                return mapBasic( dto, getFirst(formParams, prefix) );
            }
            // TODO handle list & map

            return populateDTO(dto, formParams, prefix + "." + dto.getSimpleName());
        } catch (Exception e) {
            throw badRequest(e);
        }
    }

    private String getFirst( Map<String, String[]> map, String key ) {
        String[] strings = map.get(key);
        if ( strings == null ) return null;
        return strings[0];
    }

    private Object populateDTO(Class<?> dto, Map<String, String[]> formParams, String prefix ) throws Exception {
        Object o = dto.newInstance();
        for ( Field f : o.getClass().getDeclaredFields() ) {
            if ( Modifier.isFinal(f.getModifiers()) ) continue;
            f.setAccessible(true);
            String value = getFirst(formParams, prefix + "." + f.getName());

            if ( value == null ) {
                Object innerDto = populateDTO( f.getType(), formParams, prefix + "." +f.getName() );
                f.set( o, innerDto );
            } else {
                f.set( o, mapBasic( f.getType(), value ));
            }
        }
        return o;
    }

    private Object mapBasic( Class<?> clazz, String value ) {
        if( clazz == String.class ) {
            return value;
        } else if ( clazz == Integer.class ) {
            return Integer.valueOf( value );
        } else if ( clazz == Long.class ) {
            return Long.valueOf( value );
        } else if ( clazz == Double.class ) {
            return Double.valueOf( value );
        } else if ( clazz == Boolean.class ) {
            return Boolean.valueOf( value );
        } else if ( clazz.isEnum() ) {
            return Enum.valueOf((Class<Enum>) clazz, value);
        } else {
            return null;
        }
    }

    private NotFoundException notFound() {
        return new NotFoundException();
    }

    private InternalServerErrorException internalServerError( Exception e ) {
        log.error( "Internal error", e );
        return new InternalServerErrorException();
    }

    private BadRequestException badRequest( Exception e ) {
        return new BadRequestException();
    }


}
