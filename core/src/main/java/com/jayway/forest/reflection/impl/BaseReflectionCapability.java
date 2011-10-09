package com.jayway.forest.reflection.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import com.jayway.forest.exceptions.BadRequestException;
import com.jayway.forest.reflection.Capability;
import com.jayway.forest.reflection.ReflectionUtil;

public abstract class BaseReflectionCapability extends Capability {
	public BaseReflectionCapability(String name, String documentation) {
		super(name, documentation);
	}

    protected Object mapArguments( Class<?> dto, Map<String,String[]> formParams, String prefix ) {
        try {
            if ( ReflectionUtil.basicTypes.contains( dto ) ) {
                return mapBasic( dto, getFirst(formParams, prefix) );
            }
            // TODO handle list & map

            return populateDTO(dto, formParams, prefix + "." + dto.getSimpleName());
        } catch (Exception e) {
            throw new BadRequestException();
        }
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
        } else if ( clazz == Boolean.class ) {
            return Boolean.valueOf( value );
        } else if ( clazz.isEnum() ) {
            return Enum.valueOf((Class<Enum>) clazz, value);
        } else {
            return null;
        }
    }
}
