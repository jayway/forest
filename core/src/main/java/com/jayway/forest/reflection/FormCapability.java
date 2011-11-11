package com.jayway.forest.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;

import com.jayway.forest.exceptions.BadRequestException;
import com.jayway.forest.roles.Resource;

public abstract class FormCapability extends Capability {
    public final Resource resource;
    public final Method method;

    public FormCapability(Method method, Resource resource, String name, String documentation, String rel) {
		super(name, documentation, rel);
        this.method = method;
        this.resource = resource;
	}

    protected Object[] arguments( Method m, Map<String, String[]> params ) {
        Object[] args = new Object[m.getParameterTypes().length];
        for ( int i=0; i<args.length; i++ ) {
            args[i] = mapArguments( m, i, params );
        }
        return args;
    }

    private Object mapArguments( Method m, int parameter, Map<String,String[]> formParams) {
        Class<?> dto = method.getParameterTypes()[parameter];
        String prefix = getParameterName(m, parameter);
        try {
            if ( ReflectionUtil.basicTypes.contains( dto ) ) {
                return mapBasic( dto, getFirst(formParams, prefix) );
            }
            // TODO handle list & map

            return populateDTO(dto, formParams, prefix + "." + dto.getSimpleName());
        } catch (Exception e) {
            log.error( "Could not map arguments to object. Current type: '"+prefix + "." + dto.getSimpleName() + "'",e);
            throw new BadRequestException();
        }
    }

    private String getParameterName(Method method, int parameter) {
		for (Annotation a : method.getParameterAnnotations()[parameter]) {
			if (a instanceof FormParam) {
				return ((FormParam)a).value();
			}
			if (a instanceof QueryParam) {
				return ((QueryParam)a).value();
			}
		}
		return "argument"+(parameter+1);
	}

    private String getFirst( Map<String, String[]> map, String key ) {
        String[] strings = map.get(key);
        if ( strings == null ) throw new BadRequestException();
        return strings[0];
    }

    private Object populateDTO(Class<?> dto, Map<String, String[]> formParams, String prefix ) throws Exception {
        Object o = dto.newInstance();
        for ( Field f : o.getClass().getDeclaredFields() ) {
            if ( Modifier.isFinal(f.getModifiers()) ) continue;
            f.setAccessible(true);
            try {
                f.set( o, mapBasic( f.getType(), getFirst(formParams, prefix + "." + f.getName())));
            } catch ( BadRequestException e ) {
                // two cases
                // 1: simple field and value was not given
                if ( ReflectionUtil.basicTypes.contains(f.getType()) ) continue;
                // 2: complex type, so recurse
                f.set(o, populateDTO(f.getType(), formParams, prefix + "." + f.getName()));
            }
        }
        return o;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	private Object mapBasic( Class<?> clazz, String value ) {
        if( clazz == String.class ) {
            return value;
        } else if ( clazz == Integer.class ) {
            return Integer.valueOf( value );
        } else if ( clazz == Long.class ) {
            return Long.valueOf( value );
        } else if ( clazz == Double.class ) {
            return Double.valueOf( value );
        } else if ( clazz == Float.class ) {
            return Float.valueOf( value );
        } else if ( clazz == Boolean.class ) {
            return Boolean.valueOf( value );
        } else if ( clazz.isEnum() ) {
            return Enum.valueOf((Class<Enum>) clazz, value);
        } else {
            return null;
        }
    }
}
