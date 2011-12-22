package com.jayway.forest.mediatype.json;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import com.jayway.forest.core.JSONHelper;
import com.jayway.forest.mediatype.AbstractMessageBodyWriter;
import com.jayway.forest.reflection.FormCapability;
import com.jayway.forest.reflection.CapabilityReference;
import com.jayway.forest.reflection.ReflectionUtil;
import com.jayway.forest.reflection.impl.CapabilityCreateCommand;
import com.jayway.forest.reflection.impl.CapabilityDeleteCommand;
import com.jayway.forest.reflection.impl.Parameter;
import com.jayway.forest.roles.Resource;

public abstract class JsonMessageBodyWriter<T> extends AbstractMessageBodyWriter<T> {
	
	protected final Charset charset;

	public JsonMessageBodyWriter(Class<T> clazz, Charset charset) {
		super(clazz, MediaType.APPLICATION_JSON_TYPE);
		this.charset = charset;
	}
	
    protected void appendMethod( Writer writer, CapabilityReference reference ) throws IOException {
        writer.append( "{ \"method\":\"").append(reference.httpMethod()).append("\",");
        writer.append("\"name\":").append("\"").append(reference.name() ).append("\",");
        if ( reference instanceof CapabilityCreateCommand || reference instanceof CapabilityDeleteCommand ) {
            writer.append("\"uri\":\"").append(reference.uri().substring(0, reference.uri().length() - 6 )).append("\"");
        } else {
            writer.append("\"uri\":\"").append(reference.uri()).append("\"");
        }
        if ( reference instanceof FormCapability ) {
            FormCapability base = (FormCapability) reference;
            String template = createTemplate( base.method, base.resource);
            if ( template != null ) {
                writer.append(",\"jsonTemplate\":").append(template);
            }
        }
        if ( reference.rel() != null ) {
            writer.append(",\"rel\":\"").append( reference.rel() ).append("\"");
        }
        writer.append("}");
        writer.flush();
    }

    protected void appendLink( Writer writer, String name, String link ) throws IOException {
        if ( link!= null) {
            writer.append("\"").append(name).append("\":\"").append(link).append("\",");
        }
    }

    protected void appendRawList( Writer writer, List<?> list ) throws IOException {
        if ( list == null || list.size() == 0 ) {
            writer.append("[]");
        } else {
            JSONHelper helper = new JSONHelper();
            writer.append( helper.toJSON( list ).toString() );
        }
    }

    private String createTemplate(Method method, Resource resource) {
        if ( method.getParameterTypes().length == 0 ) return null;
        List<Parameter> parameters = ReflectionUtil.parameterList(method, resource);
        final StringBuilder sb = new StringBuilder();
        if ( parameters.size() > 1 ) {
            sb.append("[");
        }
        IterableCallback.element( sb, parameters, new Callback<Parameter>(){
            public void callback(Parameter parameter) {
                jsonTemplateForParameter(sb, parameter.parameterCls(), parameter.parameterCls(), parameter.getTemplate() );
            }
        });
        if ( parameters.size() > 1 ) {
            sb.append( "]");
        }
        return sb.toString();
    }
    private void jsonTemplateForParameter(StringBuilder sb, Class<?> clazz, Type genericType, Object templateValue ) {
        if ( ReflectionUtil.basicTypes.contains( clazz ) ) {
            defaultInstanceBasic(sb, clazz, templateValue );
        } else if ( templateValue instanceof Enum) {
            defaultInstanceBasic( sb, String.class, templateValue.toString() );
        } else {
            sb.append("{");
            defaultInstanceComposed(sb, clazz, genericType, templateValue);
            sb.append("}");
        }
    }

    private void defaultInstanceComposed(final StringBuilder sb, Class<?> clazz, Type genericType, Object templateValue ) {
        if ( genericType instanceof ParameterizedType ) {
            Type rawType = ((ParameterizedType) genericType).getRawType();
            if (  List.class.isAssignableFrom( (Class) rawType ) ) {
                sb.append("[");
                Type type = ((ParameterizedType) genericType).getActualTypeArguments()[0];
                if ( templateValue != null  && templateValue instanceof List ) {
                    List<?> list = (List<?>) templateValue;
                    templateValue = null;
                    if ( !list.isEmpty() ) {
                        templateValue = list.get(0);
                    }
                    jsonTemplateForParameter(sb, (Class) type, type, templateValue );
                } else {
                    jsonTemplateForParameter(sb, (Class) type, type, null);
                }
                sb.append("]");
            } else if ( Map.class.isAssignableFrom( (Class) rawType ) ) {
                // TODO
                sb.append("{");
                //Type type = ((ParameterizedType) genericType).getActualTypeArguments()[1];
                //jsonTemplateForParameter(sb, (Class) type, type);
                sb.append("}");
            }
            // TODO maybe support general parameterized types
            return;
        }

        if ( clazz == Object.class ) {
            sb.append("{}");
            return;
        }
        final Object finalTemplateValue = templateValue;
        IterableCallback.element( sb, Arrays.asList( clazz.getDeclaredFields()), new Callback<Field>(){
            public void callback(Field field) {
                if ( Modifier.isStatic( field.getModifiers() )) return;
                if ( Modifier.isFinal(field.getModifiers())) return;
                sb.append("\"").append(field.getName()).append("\":");
                if ( finalTemplateValue != null ) {
                    try {
                        field.setAccessible(true);
                        jsonTemplateForParameter(sb, field.getType(), field.getGenericType(), field.get(finalTemplateValue));
                    } catch (IllegalAccessException e) {

                    }
                } else {
                    jsonTemplateForParameter(sb, field.getType(), field.getGenericType(), null);
                }
            }
        });
        if ( clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class ) {
            defaultInstanceComposed(sb, clazz.getSuperclass(), clazz.getSuperclass(), templateValue);
        }
    }

    private void defaultInstanceBasic(StringBuilder sb, Class<?> clazz, Object templateValue) {
        if ( clazz == String.class ) {
            if ( templateValue != null && templateValue instanceof String ) {
                sb.append("\"").append( templateValue ).append("\"");
            } else {
                sb.append("\"\"");
            }
        } else if ( templateValue != null ) {
            sb.append( templateValue );
        } else if ( clazz == Long.class || clazz == Integer.class ) {
            sb.append( 0 );
        } else if ( clazz == Double.class || clazz == Float.class ) {
            sb.append( 0.0 );
        } else if ( clazz == Boolean.class ) {
            sb.append( false );
        }
    }

    static class IterableCallback<T> {
        static <T> void element( StringBuilder sb, Iterable<T> iterable, Callback<T> callback ) {
            boolean first = true;
            for (T element : iterable) {
                if ( !first ) sb.append(",");
                else first = false;
                callback.callback( element );
            }
        }
    }

    interface Callback<T> {
        void callback( T element );
    }
}
