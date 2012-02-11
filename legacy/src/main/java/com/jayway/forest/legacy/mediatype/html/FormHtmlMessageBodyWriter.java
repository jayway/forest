package com.jayway.forest.legacy.mediatype.html;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.jayway.forest.legacy.reflection.FormCapability;
import com.jayway.forest.legacy.reflection.ReflectionUtil;
import com.jayway.forest.legacy.reflection.impl.Parameter;
import com.jayway.forest.legacy.roles.Resource;

public class FormHtmlMessageBodyWriter extends HtmlMessageBodyWriter<FormCapability> {

	public FormHtmlMessageBodyWriter(Charset charset, String cssUrl) {
		super(FormCapability.class, charset, cssUrl);
	}

	@Override
	public void writeTo(FormCapability baseReflection, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream out) throws IOException, WebApplicationException {
    	String httpMethod = "GET";
    	if (!baseReflection.httpMethod().equals("GET")) {
    		httpMethod = "POST";
    	}
        createForm(out, baseReflection.method, httpMethod, baseReflection.resource);
	}
    /**
     * Generates an HTML form based on the method argument.
     * It will reflectively look at the argument for the method,
     * which has to be a single argument of DTO type, and
     * construct the form based on that
     * @param out 
     *
     * @param method
     * @return html form getting the parameters needed for the method
     * @throws IOException 
     */
    protected void createForm( OutputStream out, Method method, String httpMethod, Resource resource ) throws IOException {
        List<Parameter> parameters = ReflectionUtil.parameterList(method, resource);
        Class<?>[] types = method.getParameterTypes();
        OutputStreamWriter writer = new OutputStreamWriter( out, charset);
        writeHeader(writer);
        writer.append( "<form name='generatedform' action='").append(method.getName()).
                append("' method='").append(httpMethod).append("' >" );

        for ( int i=0; i<parameters.size(); i++ ) {
            Parameter parameter = parameters.get(i);
            htmlForParameter(parameter.getName(), parameter.parameterCls(), writer, parameter.parameterCls().getSimpleName(), parameter.getTemplate());
        }
        writer.append( "<input type='submit' /></form>" );
        writeFooter(writer);
        writer.flush();
    }

    private void htmlForParameter(String legend, Class<?> dto, Writer writer, String typeName, Object templateValue) throws IOException {
        writer.append("<fieldset><legend>").append(legend).append("</legend>");
        if ( ReflectionUtil.basicTypes.contains( dto ) ) {
            // special case for text area
            if ( templateValue != null && templateValue instanceof String && ( ((String) templateValue).length()> 20)) {
                String value = (String) templateValue;
                int rows = lines( value ) + 2;
                writer.append(typeName).append(": <textarea rows='").append( "" + rows ).append("' cols='80' name='");
                writer.append(legend).append("'>").append(value).append("</textarea></br>");
            } else {
                writer.append(typeName).append(": <input type='text' ");
                if ( templateValue != null && ReflectionUtil.basicTypes.contains( templateValue.getClass() )) {
                    writer.append(" value='").append(templateValue.toString()).append("' ");
                }
                writer.append("name='").append( legend ).append("'/></br>");
            }
        } else if ( List.class.isAssignableFrom( dto ) ) {
            // TODO create textarea and accept a comma separated list of values
        } else if ( Map.class.isAssignableFrom( dto ) ) {
            // TODO
        } else {
            // dto type
            htmlForComposite(dto.getSimpleName(), dto, writer, legend + "." + dto.getSimpleName(), templateValue);
        }

        writer.append("</fieldset>");
    }


    private void htmlForComposite(String legend, Class<?> dto, Writer writer, String fieldPath, Object templateValue ) throws IOException {
        writer.append("<fieldset><legend>").append(legend).append("</legend>");
        for ( Field f : dto.getDeclaredFields() ) {
            if ( Modifier.isFinal(f.getModifiers())) continue;
            if ( Modifier.isStatic(f.getModifiers())) continue;
            Object fieldValue = null;
            if ( templateValue != null ) {
                f.setAccessible( true );
                try {
                    fieldValue = f.get(templateValue);
                } catch (IllegalAccessException e) {
                    // ignore
                }
            }
            String name = f.getName();
            Class<?> type = f.getType();
            // this must be one of the getters
            if ( ReflectionUtil.basicTypes.contains(  type ) ) {
                writer.append(name).append(": <input type='");
                writer.append( name.equals("password")? "password": "text" );
                if ( fieldValue != null ) {
                    writer.append("' value='").append(fieldValue.toString());
                }
                writer.append("' name='").append( fieldPath ).append( "." ).append( name).append("'/></br>");
            } else if ( type.isEnum() ) {
                // TODO handle enums
                writer.append(name).append( ": <select name='").append( fieldPath ).append( "." ).append(name).append("'>");
                for ( Object o : type.getEnumConstants() ) {
                    writer.append( "<option value='").append(o.toString()).append("'>").append(o.toString()).append("</option>");
                }
                writer.append("</select></br>");
            } else {
                // for now assume DTO subtype
                // TODO List & Map, (any other???)
                htmlForComposite(name, type, writer, fieldPath + "." + name, fieldValue);
            }
        }
        writer.append("</fieldset>");
    }
    private int lines(String contents ) {
        int count = 0;
        for (int i=0; i < contents.length(); i++) {
            if (contents.charAt(i) == '\n') {
                count++;
            }
        }
        return count;
    }
}
