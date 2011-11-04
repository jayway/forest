package com.jayway.forest.core;

import com.jayway.forest.exceptions.BadRequestException;
import com.jayway.forest.exceptions.InternalServerErrorException;
import com.jayway.forest.reflection.ReflectionUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.InputStream;
import java.lang.reflect.*;
import java.util.*;
import java.util.Map.Entry;

/**
 */
public class JSONHelper {

    @SuppressWarnings({ "unchecked" })
    public Object toJSON(final Object dto ) {
        if ( dto == null ) return null;
        // basic types or ENUM
        if ( dto instanceof Enum ) {
            return dto.toString();
        }

        if ( ReflectionUtil.basicTypes.contains( dto.getClass() ) ) {
            return dto;
        }

        // JSONArray
        if ( dto instanceof List ) {
            JSONArray arrayResult = new JSONArray();
            for ( Object o : (List<?>)dto ) {
                arrayResult.add( toJSON(o) );
            }
            return arrayResult;
        }

        // JSONObject
        if ( dto instanceof Map ) {
            JSONObject mapResult = new JSONObject();
            Map<?,?> map = (Map<?,?>) dto;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                mapResult.put( entry.getKey(), toJSON(entry.getValue()) );
            }
            return mapResult;
        }

        // JSONObject
        return allFields( dto.getClass(), dto, new JSONObject() );
    }

    public JSONObject allFields( Class<?> clazz, Object instance, JSONObject result ) {
        for (Field field : clazz.getDeclaredFields() ) {
            if (Modifier.isFinal(field.getModifiers()) ) continue;
            //if (Modifier.isStatic(field.getModifiers()) ) continue;
            field.setAccessible( true );
            try {
                Object value = field.get( instance );
                if ( value != null ) {
                    result.put(field.getName(), toJSON( value ));
                }
            } catch (IllegalAccessException e) { }
        }
        if ( clazz.getSuperclass() != Object.class ) return allFields( clazz.getSuperclass(), instance, result );
        return result;
    }

    public Object[] handleArguments(Method m, InputStream stream) {
        int argumentCount = m.getParameterTypes().length;
        if ( argumentCount == 0 ) return new Object[0];

        String jsonContent = convertWholeStreamToString(stream);
        Object parse = JSONValue.parse(jsonContent);
        if ( parse == null ) {
            throw new BadRequestException( "Could not parse: "+ jsonContent);
        }

        if ( argumentCount == 1 ) {
            return new Object[] { handleArgument( m.getParameterTypes()[0], m.getGenericParameterTypes()[0], parse ) };
        }

        // several arguments must passed in a JSON array
        if ( !(parse instanceof JSONArray) ) throw new BadRequestException("More than one argument needs to be passed in a JSON array");
        JSONArray argumentArray = (JSONArray) parse;
        if ( argumentArray.size() != argumentCount ) throw new BadRequestException(String.format("Mismatch between number of arguments (%d) and array size (%d)", argumentCount, argumentArray.size()));

        // more than one argument
        Object[] arguments = new Object[ argumentCount ];
        for ( int i=0; i<argumentCount; i++ ) {
            arguments[i] = handleArgument( m.getParameterTypes()[i], m.getGenericParameterTypes()[i], argumentArray.get( i ) );
        }
        return arguments;

    }

    private String convertWholeStreamToString(InputStream stream) {
        return new Scanner(stream).useDelimiter("\\A").next();
    }

    @SuppressWarnings("unchecked")
    public <T> T fromJSON( Class<T> clazz, Object jsonValue ) {
        return (T) handleArgument(clazz, clazz, jsonValue);
    }

    private Object handleArgument( Class<?> argumentClass, Type type, Object jsonValue ) {
        if ( argumentClass.isEnum() ) {
            return Enum.valueOf((Class<Enum>) argumentClass, jsonValue.toString());
        }

        // basic type
        if ( ReflectionUtil.basicTypes.contains( argumentClass ) ) {
            Object value = basicType(jsonValue, argumentClass);
            if ( argumentClass.isAssignableFrom( value.getClass() )) {
                return value;
            } else {
                throw new BadRequestException(String.format("Couldn't assign value of class %s to argument of class %s", jsonValue.getClass(), argumentClass));
            }
        }

        // List
        if ( List.class.isAssignableFrom( argumentClass ) ) {
            ArrayList<Object> list = new ArrayList<Object>();
            if ( jsonValue instanceof JSONArray ) {
                Type typeArgument;
                Class<?> typeClass;
                if ( type instanceof ParameterizedType) {
                    typeArgument = ((ParameterizedType) type).getActualTypeArguments()[0];
                    if ( typeArgument instanceof ParameterizedType ) {
                        typeClass = (Class<?>) ((ParameterizedType) typeArgument).getRawType();
                    } else {
                        typeClass = (Class<?>) typeArgument;
                    }
                } else {
                    typeArgument = Object.class;
                    typeClass = Object.class;
                }
                for ( Object elm : (JSONArray) jsonValue ) {
                    list.add( handleArgument( typeClass, typeArgument, elm ) );
                }
            } else {
                throw new BadRequestException(String.format("Couldn't assign value of class %s to argument of class %s", jsonValue.getClass(), argumentClass));
            }
            return list;
        }

        // Map
        if ( Map.class.isAssignableFrom( argumentClass ) ) {
            HashMap<Object,Object> map = new HashMap<Object,Object>();
            if ( jsonValue instanceof JSONObject) {
                Type keyType;
                Type valueType;
                Class<?> keyClass;
                Class<?> valueClass;
                if ( type instanceof ParameterizedType ) {
                    keyType = ((ParameterizedType) type).getActualTypeArguments()[0];
                    valueType = ((ParameterizedType) type).getActualTypeArguments()[1];
                    // if typeArgument instanceof ParameterizedType...
                    keyClass = keyType.getClass();
                    valueClass = valueType.getClass();
                } else {
                    keyType = valueType = keyClass = valueClass = Object.class;
                }
                JSONObject obj = (JSONObject) jsonValue;
                for (Object e : obj.entrySet()) {
                    Map.Entry<?, ?> entry = (Entry<?, ?>) e;
                    map.put( handleArgument( keyClass, keyType, entry.getKey()),
                            handleArgument( valueClass, valueType, entry.getValue()));
                }
            } else {
                throw new BadRequestException(String.format("Couldn't assign value of class %s to argument of class %s", jsonValue.getClass(), argumentClass));
            }
            return map;
        }

        // Composite
        if ( jsonValue instanceof JSONObject ) {
            JSONObject json = (JSONObject) jsonValue;
            try {
                Object composite = argumentClass.newInstance();
                for ( Field field: argumentClass.getDeclaredFields() ) {
                    if ( Modifier.isFinal(field.getModifiers())) continue;
                    Object value = json.get(field.getName());
                    if ( value == null) continue;
                    field.setAccessible( true );
                    field.set( composite, handleArgument( field.getType(), field.getType(), value));
                }
                return composite;
            } catch (InstantiationException e) {
                throw new InternalServerErrorException(e.getMessage());
            } catch (IllegalAccessException e) {
                throw new InternalServerErrorException(e.getMessage());
            }
        } else {
            throw new BadRequestException(String.format("Couldn't find any matching clause for value of class %s and argument of class %s", jsonValue.getClass(), argumentClass));
        }
    }

    private Object basicType( Object value, Class<?> type ) {
        if ( value instanceof Long && type == Integer.class ) {
            return ((Long) value).intValue();
        } else if ( value instanceof Double && type == Float.class ) {
            return ((Double) value).floatValue();
        } else {
            return value;
        }
    }

}
