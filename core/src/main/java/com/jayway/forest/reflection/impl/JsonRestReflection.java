package com.jayway.forest.reflection.impl;

import com.jayway.forest.core.JSONHelper;
import com.jayway.forest.reflection.Capabilities;
import com.jayway.forest.reflection.CapabilityReference;
import com.jayway.forest.reflection.ReflectionUtil;
import com.jayway.forest.reflection.RestReflection;
import com.jayway.forest.roles.Linkable;
import com.jayway.forest.roles.Resource;
import com.jayway.forest.servlet.Response;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.*;
import java.nio.charset.Charset;
import java.util.*;

public final class JsonRestReflection extends BasicRestReflection implements RestReflection {

    public static final RestReflection INSTANCE = new JsonRestReflection(Charset.forName("UTF-8"));

    private JsonRestReflection(Charset charset) {
    	super(charset);
    }

    @Override
    public void renderCapabilities(OutputStream out, Capabilities capabilities) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter( out, charset);
        writer.append("[");
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
            toMapEntries(all, writer);
        }
        writer.append("]");
        writer.flush();
    }

    private void toMapEntries(List<CapabilityReference> list, Writer results) throws IOException {
        boolean first = true;
        for (CapabilityReference method : list) {
            if ( !first ) results.append( ",\n");
            else first = false;
            appendMethod(results, method);
        }
    }

    @Override
    public void renderForm(OutputStream out, BaseReflection baseReflection ) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter( out, charset);
        appendMethod(writer, baseReflection );
        writer.flush();
    }

    private void appendMethod( Writer writer, CapabilityReference reference ) throws IOException {
        writer.append( "{ \"method\":\"").append(reference.httpMethod()).append("\",");
        writer.append("\"name\":").append("\"").append(reference.name() ).append("\",");
        if ( reference instanceof CapabilityCreateCommand || reference instanceof CapabilityDeleteCommand ) {
            writer.append("\"href\":\"").append(reference.href().substring(0, reference.href().length() - 6 )).append("\"");
        } else {
            writer.append("\"href\":\"").append(reference.href()).append("\"");
        }
        if ( reference instanceof BaseReflection ) {
            BaseReflection base = (BaseReflection) reference;
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

    @Override
    public void renderListResponse(OutputStream out, PagedSortedListResponse response) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter( out, charset);
        writer.append("{");
        appendLink( writer, "next", response.getNext() );
        appendLink(writer, "previous", response.getPrevious());
        if ( response.getOrderByAsc() != null ) {
            // TODO
        }
        if ( response.getOrderByDesc() != null ) {
            // TODO
        }
        writer.append("\"page\":").append( emptyOrString(response.getPage()) ).append(",");
        writer.append("\"pageSize\":").append( emptyOrString(response.getPageSize()) ).append(",");
        writer.append("\"totalPages\":").append( emptyOrString(response.getTotalPages()) ).append(",");
        writer.append("\"totalElements\":").append( emptyOrString(response.getTotalElements()) ).append(",");
        writer.append("\"list\":");
        appendRawList( writer, response.getList() );
        writer.append("}");
        writer.flush();
    }

    @Override
    public void renderQueryResponse(OutputStream out, Object responseObject) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter( out, charset);
        if ( responseObject instanceof String ) {
        	writer.write("\"");
        	writer.write(responseObject.toString());
        	writer.write("\"");
        } else {
        	writer.write(new JSONHelper().toJSON(responseObject).toString());
        }
        writer.flush();
    }

    @Override
    public void renderError(OutputStream out, Response response) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter( out, charset);
        writer.write(response.message());
        writer.flush();
    }

    @Override
    public void renderCreatedResponse(OutputStream out, Linkable linkable) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter( out, charset);
        appendMethod(writer, new CapabilityLinkable( linkable ) );
        writer.flush();
    }

    private void appendLink( Writer writer, String name, String link ) throws IOException {
        if ( link!= null) {
            writer.append("\"").append(name).append("\":\"").append(link).append("\",");
        }
    }

    private void appendRawList( Writer writer, List<?> list ) throws IOException {
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
