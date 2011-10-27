package com.jayway.forest.reflection.impl;

import com.jayway.forest.core.JSONHelper;
import com.jayway.forest.reflection.Capabilities;
import com.jayway.forest.reflection.CapabilityReference;
import com.jayway.forest.reflection.ReflectionUtil;
import com.jayway.forest.reflection.RestReflection;
import com.jayway.forest.roles.Linkable;
import com.jayway.forest.roles.Resource;
import com.jayway.forest.servlet.Response;

import java.lang.reflect.*;
import java.util.*;

public final class JsonRestReflection implements RestReflection {

    public static final RestReflection INSTANCE = new JsonRestReflection();

    private JsonRestReflection() {
    }

    @Override
    public Object renderCapabilities(Capabilities capabilities) {
        StringBuilder results = new StringBuilder( );
        results.append("[");
        List<CapabilityReference> all = new LinkedList<CapabilityReference>();
        all.addAll( capabilities.getQueries() );
        all.addAll( capabilities.getCommands() );
        all.addAll( capabilities.getResources() );
        for (Linkable link : capabilities.getDiscoveredLinks()) {
            all.add(new CapabilityLinkable(link));
        }
        if ( capabilities.getIdResource() != null ) {
            all.add( capabilities.getIdResource());
        }
        if ( !all.isEmpty() ) {
            toMapEntries(all, results);
        }
        results.append("]");
        return results.toString();
    }

    private void toMapEntries(List<CapabilityReference> list, StringBuilder results) {
        boolean first = true;
        for (CapabilityReference method : list) {
            if ( !first ) results.append( ",\n");
            else first = false;
            appendMethod(results, method);
        }
    }

    @Override
    public Object renderQueryForm(BaseReflection baseReflection ) {
        return appendMethod( new StringBuilder(), baseReflection ).toString();
    }

    @Override
    public Object renderCommandCreateForm(BaseReflection baseReflection) {
        return appendMethod(new StringBuilder(), baseReflection).toString();
    }

    @Override
    public Object renderCommandDeleteForm(BaseReflection baseReflection) {
        return appendMethod( new StringBuilder(), baseReflection ).toString();
    }

    @Override
    public Object renderCommandForm( BaseReflection baseReflection ) {
        return appendMethod(new StringBuilder(), baseReflection ).toString();
    }

    private StringBuilder appendMethod( StringBuilder sb, CapabilityReference reference ) {
        sb.append( "{ \"method\":\"").append(reference.httpMethod()).append("\",");
        sb.append("\"name\":").append("\"").append(reference.name() ).append("\",");
        sb.append("\"href\":\"").append(reference.href()).append("\"");
        if ( reference instanceof BaseReflection ) {
            BaseReflection base = (BaseReflection) reference;
            String template = createTemplate( base.method, base.resource);
            if ( template != null ) {
                sb.append(",\"jsonTemplate\":").append(template);
            }
        }
        if ( reference.rel() != null ) {
            sb.append(",\"rel\":\"").append( reference.rel() ).append("\"");
        }
        sb.append("}");
        return sb;
    }

    @Override
    public Object renderListResponse(PagedSortedListResponse response) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        appendLink( sb, "next", response.getNext() );
        appendLink(sb, "previous", response.getPrevious());
        if ( response.getOrderByAsc() != null ) {
            // TODO
        }
        if ( response.getOrderByDesc() != null ) {
            // TODO
        }
        sb.append("\"page\":").append( response.getPage() ).append(",");
        sb.append("\"pageSize\":").append( response.getPageSize() ).append(",");
        sb.append("\"totalPages\":").append( response.getTotalPages() ).append(",");
        sb.append("\"totalElements\":").append( response.getTotalElements() ).append(",");
        sb.append("\"list\":");
        appendRawList( sb, response.getList() );
        sb.append("}");
        return sb.toString();
    }

    @Override
    public Object renderQueryResponse(Object responseObject) {
        if (responseObject instanceof String) {
            return "\"" + responseObject + "\"";
        } else {
            return new JSONHelper().toJSON(responseObject);
        }
    }

    @Override
    public Object renderError(Response response) {
        return response.message();
    }

    @Override
    public Object renderCreatedResponse(Linkable linkable) {
        StringBuilder sb = new StringBuilder();
        appendMethod(sb, new CapabilityLinkable( linkable ) );
        return sb.toString();
    }

    private void appendLink( StringBuilder sb, String name, String link ) {
        if ( link!= null) {
            sb.append("\"").append(name).append("\":\"").append(link).append("\",");
        }
    }

    private void appendRawList( StringBuilder sb, List<?> list ) {
        if ( list == null || list.size() == 0 ) {
            sb.append("[]");
        } else {
            JSONHelper helper = new JSONHelper();
            sb.append( helper.toJSON( list ).toString() );
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
