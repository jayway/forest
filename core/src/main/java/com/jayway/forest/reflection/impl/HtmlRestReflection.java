package com.jayway.forest.reflection.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import com.jayway.forest.core.JSONHelper;
import com.jayway.forest.exceptions.AbstractHtmlException;
import com.jayway.forest.reflection.Capabilities;
import com.jayway.forest.reflection.Capability;
import com.jayway.forest.reflection.ReflectionUtil;
import com.jayway.forest.reflection.RestReflection;
import com.jayway.forest.roles.Linkable;
import com.jayway.forest.roles.Resource;
import com.jayway.forest.servlet.Response;

public final class HtmlRestReflection extends BasicRestReflection implements RestReflection {

    public static final RestReflection DEFAULT = new HtmlRestReflection(Charset.forName("UTF-8"), null);
	private final String cssUrl;

    public HtmlRestReflection(Charset charset, String cssUrl) {
    	super(charset);
		this.cssUrl = cssUrl;
    }

	@Override
	public void renderQueryResponse(OutputStream out, Object responseObject) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter( out, charset);
        if ( responseObject instanceof String ) {
        	writer.write(responseObject.toString());
        } else {
        	writer.write(new JSONHelper().toJSON(responseObject).toString());
        }
        writer.flush();
	}

    @Override
    public void renderCapabilities(OutputStream out, Capabilities capabilities) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter( out, charset);
        writeHeader(writer);
        writer.append("<h1>"+ capabilities.getName()  +"</h1>");
        if (!capabilities.getQueries().isEmpty()) {
            writer.append("<h2>Queries</h2>");
            writer.append("<ul>");
            for (Capability method: capabilities.getQueries()) {
                appendMethod( writer, method );
            }
            writer.append("</ul>");
        }
        if (!capabilities.getCommands().isEmpty()) {
            writer.append("<h2>Commands</h2>");
            writer.append("<ul>");
            for (Capability method: capabilities.getCommands()) {
                appendMethod( writer, method );
            }
            writer.append("</ul>");
        }
        if (!capabilities.getResources().isEmpty() || !capabilities.getDiscoveredLinks().isEmpty()) {
            writer.append("<h2>Sub Resources</h2>");
            writer.append("<ul>");

            for (Capability method: capabilities.getResources()) {
                appendMethod(writer, method );
            }
            for (Linkable resource: capabilities.getDiscoveredLinks()) {
                if ( resource == null ) continue;
                writer.append("<li><a href='").append(resource.getHref()).append("'>").append(resource.getName()).append("</a></li>");
            }
            writer.append("</ul>");
        }
        if ( capabilities.getPagedSortedListResponse() != null ) {
            PagedSortedListResponse<?> response = capabilities.getPagedSortedListResponse();
            appendPagingInfo( writer, response, true);
            if ( response.getOrderByAsc() != null ) {
                appendAnchor( writer, response.getOrderByAsc().get( "name"), "asc", true);
            }
            if ( response.getOrderByDesc() != null) {
                appendAnchor( writer, response.getOrderByDesc().get( "name"), "desc", true);
            }
        }

        if (capabilities.getDescriptionResult() != null ) {
            writer.append("<h2>Description</h2>");
            writer.append( new JSONHelper().toJSON( capabilities.getDescriptionResult() ).toString());
        }
        writeFooter(writer);
        writer.flush();
    }

	private void writeFooter(OutputStreamWriter writer) throws IOException {
		writer.append("</body></html>");
	}

	private void writeHeader(OutputStreamWriter writer) throws IOException {
		writer.append( "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=").append(charset.name()).append("\">");
        if (cssUrl != null) {
        	writer.append("<link rel=\"stylesheet\" href=\"").append(cssUrl).append("\" type=\"text/css\" />");
        }
        writer.append("</head><body>" );
	}

    private void appendMethod(Writer writer, Capability method ) throws IOException {
    	writer.append("<li><a href='").append(method.href());
    	writer.append("'>").append( method.name() ).append("</a>");
        if ( method.isDocumented() ) {
        	writer.append(" <i>(").append( method.documentation() ).append("</i>)");
        }
        writer.append("</li>");
    }

    @Override
    public void renderForm(OutputStream out, BaseReflection baseReflection) throws IOException {
    	String httpMethod = "GET";
    	if (!baseReflection.httpMethod().equals("GET")) {
    		httpMethod = "POST";
    	}
        createForm(out, baseReflection.method, httpMethod, baseReflection.resource);
    }

    private void appendPagingInfo( Writer writer, PagedSortedListResponse response, boolean stripName ) throws IOException {
        writer.append("Page ").append( response.getPage().toString() ).append( " of ").append( response.getTotalPages().toString() );

        appendAnchor( writer, response.getPrevious(), "previous", stripName );
        appendAnchor( writer, response.getNext(), "next" , stripName);
    }

    @Override
    public void renderListResponse(OutputStream out, PagedSortedListResponse<?> response ) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter( out, charset);
        writeHeader(writer);
        writer.append( "<h1>").append(response.getName()).append("</h1>");
        appendPagingInfo( writer, response, false );

        List<?> list = response.getList();
        if ( !list.isEmpty() ) {
            if ( list.get( 0 ) instanceof Linkable ) {
                if ( response.getOrderByAsc() != null && response.getOrderByDesc() != null ) {
                    appendAnchor( writer, response.getOrderByAsc().get( "name"), "asc", false);
                    appendAnchor( writer, response.getOrderByDesc().get( "name"), "desc", false);
                }

                if (list.get(0).getClass() == Linkable.class) {
                    writer.append( "<ul>" );
                    for (Object elm : list) {
                        renderLinkable(writer, (Linkable) elm);
                    }
                    writer.append( "</ul>" );
                }
            }
            renderTable(writer, list, response );
        }
        writeFooter(writer);
        writer.flush();
    }

    @Override
    public void renderError(OutputStream out, Response response ) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter( out, charset);
        if ( response.status() == 405) {
            writer.write(response.message());
            writer.flush();
            return;
        }
        writeHeader(writer);
        writer.append("<h1>HTTP Error ").append(response.status().toString()).append("</h1>");
        String description = AbstractHtmlException.messageMapping.get(response.status());
        if ( description != null ) {
            writer.append("<code>").append( description).append("</code>");
        }
        writer.append("<h2>Message</h2>").append( response.message() );
        writeFooter(writer);
        writer.flush();
    }

    @Override
    public void renderCreatedResponse(OutputStream out, Linkable linkable) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter( out, charset);
        writeHeader(writer);
        writer.write("<code>Location:</code> <a href='" + linkable.getHref() + "' rel='"+linkable.getRel()+"'>"+linkable.getName() +"</a>");
        writeFooter(writer);
        writer.flush();
    }

    private void renderTable(Writer writer, List<?> list, PagedSortedListResponse response ) throws IOException {
        writer.append("<table><tr>");
        Object element = list.get(0);
        if ( element instanceof Linkable ) {
            writer.append("<th>href");
            generateSortOption( writer, "href", response );
            writer.append("</th>");
        }
        renderTableHeader( writer, element, element.getClass(), response );
        writer.append("</tr>");
        for ( Object elm: list ) {
            writer.append("<tr>");
            if ( elm instanceof Linkable ) {
                writer.append("<td>");
                appendAnchor( writer, ((Linkable) elm).getHref(), ((Linkable) elm).getName(), false);
                writer.append("</td>");
                renderTableRow(writer, elm.getClass(), elm );
            } else {
                renderTableRow(writer, elm.getClass(), elm );
            }
            writer.append("</tr>");
        }
        writer.append("</table>");
    }

    private void appendAnchor(Writer writer, String id, String name, boolean stripMethodName ) throws IOException {
        if ( id == null ) return;
        if ( stripMethodName ) {
            id = id.substring( id.indexOf( "?" ) );
        }
        writer.append( "  <a href=\"" ).append( id ).append("\">").append(name).append("</a>");
    }

    private void renderTableHeader( final Writer writer, Object instance, Class clazz, final PagedSortedListResponse response ) throws IOException {
        if ( clazz == Object.class ) return;
        iterateFields(clazz, instance, new FieldIterator() {
            public void field(Field field) throws IOException {
                writer.append("<th>").append(field.getName());
                generateSortOption(writer, field.getName(), response);
                writer.append("</th>");
            }
        });
        renderTableHeader( writer, instance, clazz.getSuperclass(), response );
    }

    private void generateSortOption(Writer writer, String name, PagedSortedListResponse<?> response) throws IOException {
        String asc = response.getOrderByAsc().get(name);
        String desc = response.getOrderByDesc().get(name);
        if ( asc != null ) {
            writer.append( " <a href=\"" ).append( asc ).append( "\">^</a>");
        }
        if ( desc != null ) {
            writer.append( " <a href=\"").append(desc).append("\">v</a>");
        }

    }

    private void renderTableRow( final Writer writer, Class clazz, final Object element ) throws IOException {
        if ( clazz == Object.class ) return;
        iterateFields( clazz, element, new FieldIterator() {
            public void field(Field field) throws IllegalAccessException, IOException {
                writer.append("<td>").append(emptyOrString(field.get(element))).append("</td>");
            }
        });
        renderTableRow(writer, clazz.getSuperclass(), element );
    }

    private void iterateFields( Class clazz, Object instance, FieldIterator callback ) throws IOException {
        for (Field field : clazz.getDeclaredFields()) {
            if ( Modifier.isFinal(field.getModifiers())) continue;
            if ( Modifier.isStatic( field.getModifiers() )) continue;
            if ( instance instanceof Linkable && field.getName().equals( "href") ) continue;
            if ( instance instanceof Linkable && field.getName().equals( "rel") ) continue;
            field.setAccessible(true);
            try {
                callback.field( field );
            } catch (IllegalAccessException e) {
                // ignore
            }
        }
    }

    public interface FieldIterator {
        void field( Field field ) throws IllegalAccessException, IOException;
    }

    private void renderLinkable(Writer writer, Linkable link) throws IOException {
        writer.append("<li>");
        appendAnchor( writer, link.getHref(), link.getName(), false );
        writer.append( "</li>" );
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
            htmlForParameter("argument" + (i + 1), parameter.parameterCls(), writer, parameter.parameterCls().getSimpleName(), parameter.getTemplate());
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
