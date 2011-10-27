package com.jayway.forest.reflection.impl;

import com.jayway.forest.core.JSONHelper;
import com.jayway.forest.exceptions.AbstractHtmlException;
import com.jayway.forest.reflection.*;
import com.jayway.forest.roles.Linkable;
import com.jayway.forest.roles.Resource;
import com.jayway.forest.servlet.Response;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

public final class HtmlRestReflection implements RestReflection {

    public static final RestReflection INSTANCE = new HtmlRestReflection();

    private HtmlRestReflection() {
    }

    @Override
    public Object renderCapabilities(Capabilities capabilities) {
        StringBuilder results = new StringBuilder( "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"></head><body>" );
        results.append("<h1>"+ capabilities.getName()  +"</h1>");
        if (!capabilities.getQueries().isEmpty()) {
            results.append("<h2>Queries</h2>");
            results.append("<ul>");
            for (Capability method: capabilities.getQueries()) {
                appendMethod( results, method );
            }
            results.append("</ul>");
        }
        if (!capabilities.getCommands().isEmpty()) {
            results.append("<h2>Commands</h2>");
            results.append("<ul>");
            for (Capability method: capabilities.getCommands()) {
                appendMethod( results, method );
            }
            results.append("</ul>");
        }
        if (!capabilities.getResources().isEmpty() || !capabilities.getDiscoveredLinks().isEmpty()) {
            results.append("<h2>Sub Resources</h2>");
            results.append("<ul>");

            for (Capability method: capabilities.getResources()) {
                appendMethod(results, method );
            }
            for (Linkable resource: capabilities.getDiscoveredLinks()) {
                if ( resource == null ) continue;
                results.append("<li><a href='").append(resource.getHref()).append("'>").append(resource.getName()).append("</a></li>");
            }
            results.append("</ul>");
        }
        if ( capabilities.getPagedSortedListResponse() != null ) {
            PagedSortedListResponse<?> response = capabilities.getPagedSortedListResponse();
            appendPagingInfo( results, response, true);
            if ( response.getOrderByAsc() != null ) {
                appendAnchor( results, response.getOrderByAsc().get( "name"), "asc", true);
            }
            if ( response.getOrderByDesc() != null) {
                appendAnchor( results, response.getOrderByDesc().get( "name"), "desc", true);
            }
        }

        if (capabilities.getDescriptionResult() != null ) {
            results.append("<h2>Description</h2>").append( new JSONHelper().toJSON( capabilities.getDescriptionResult() ));
        }
        results.append("</body></html>");
        return results.toString();
    }

    private void appendMethod( StringBuilder sb, Capability method ) {
        sb.append("<li><a href='").append(method.href());
        sb.append("'>").append( method.name() ).append("</a>");
        if ( method.isDocumented() ) {
            sb.append(" <i>(").append( method.documentation() ).append("</i>)");
        }
        sb.append("</li>");
    }

    @Override
    public Object renderQueryForm( BaseReflection  baseReflection ) {
        return createForm(baseReflection.method, "GET", baseReflection.resource);
    }

    @Override
    public Object renderCommandDeleteForm(BaseReflection  baseReflection) {
        return createForm(baseReflection.method, "POST", baseReflection.resource);
    }

    @Override
    public Object renderCommandCreateForm(BaseReflection  baseReflection) {
        return createForm(baseReflection.method, "POST", baseReflection.resource);
    }

    @Override
    public Object renderCommandForm(BaseReflection  baseReflection) {
        return createForm(baseReflection.method, "POST", baseReflection.resource);
    }

    private void appendPagingInfo( StringBuilder sb, PagedSortedListResponse response, boolean stripName ) {
        sb.append("Page ").append( response.getPage() ).append( " of ").append( response.getTotalPages() );

        appendAnchor( sb, response.getPrevious(), "previous", stripName );
        appendAnchor( sb, response.getNext(), "next" , stripName);
    }

    @Override
    public Object renderListResponse(PagedSortedListResponse<?> response ) {
        StringBuilder htmlListResponse = new StringBuilder( "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"></head><body>" );
        htmlListResponse.append( "<h1>").append(response.getName()).append("</h1>");
        appendPagingInfo( htmlListResponse, response, false );

        List<?> list = response.getList();
        if ( !list.isEmpty() ) {
            if ( list.get( 0 ) instanceof Linkable ) {
                if ( response.getOrderByAsc() != null && response.getOrderByDesc() != null ) {
                    appendAnchor( htmlListResponse, response.getOrderByAsc().get( "name"), "asc", false);
                    appendAnchor( htmlListResponse, response.getOrderByDesc().get( "name"), "desc", false);
                }

                if (list.get(0).getClass() == Linkable.class) {
                    htmlListResponse.append( "<ul>" );
                    for (Object elm : list) {
                        renderLinkable(htmlListResponse, (Linkable) elm);
                    }
                    htmlListResponse.append( "</ul>" );
                    return htmlListResponse;
                }
            }
            renderTable(htmlListResponse, list, response );
        }
        return htmlListResponse.append("</body></html>");
    }

    @Override
    public Object renderQueryResponse(Object responseObject) {
        if ( responseObject instanceof String ) {
            return responseObject.toString();
        } else {
            // complex object are JSON serialized for html output
            return new JSONHelper().toJSON(responseObject).toString();
        }
    }

    @Override
    public Object renderError( Response response ) {
        if ( response.status() == 405) {
            return response.message();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<html><h1>HTTP Error ").append(response.status()).append("</h1>");
        String description = AbstractHtmlException.messageMapping.get(response.status());
        if ( description != null ) {
            sb.append("<code>").append( description).append("</code>");
        }
        sb.append("<h2>Message</h2>").append( response.message() ).append("</html>");
        return sb.toString();
    }

    @Override
    public Object renderCreatedResponse(Linkable linkable) {
        return "<code>Location:</code> <a href='" + linkable.getHref() + "' rel='"+linkable.getRel()+"'>"+linkable.getName() +"</a>";
    }

    private void renderTable(StringBuilder sb, List<?> list, PagedSortedListResponse response ) {
        sb.append("<table><tr>");
        Object element = list.get(0);
        if ( element instanceof Linkable ) {
            sb.append("<th>href");
            generateSortOption( sb, "href", response );
            sb.append("</th>");
        }
        renderTableHeader( sb, element, element.getClass(), response );
        sb.append("</tr>");
        for ( Object elm: list ) {
            sb.append("<tr>");
            if ( elm instanceof Linkable ) {
                sb.append("<td>");
                appendAnchor( sb, ((Linkable) elm).getHref(), ((Linkable) elm).getName(), false);
                sb.append("</td>");
                renderTableRow(sb, elm.getClass(), elm );
            } else {
                renderTableRow(sb, elm.getClass(), elm );
            }
            sb.append("</tr>");
        }
        sb.append("</table>");
    }

    private void appendAnchor( StringBuilder sb, String id, String name, boolean stripMethodName ) {
        if ( id == null ) return;
        if ( stripMethodName ) {
            id = id.substring( id.indexOf( "?" ) );
        }
        sb.append( "  <a href=\"" ).append( id ).append("\">").append(name).append("</a>");
    }

    private void renderTableHeader( final StringBuilder sb, Object instance, Class clazz, final PagedSortedListResponse response ) {
        if ( clazz == Object.class ) return;
        iterateFields(clazz, instance, new FieldIterator() {
            public void field(Field field) {
                sb.append("<th>").append(field.getName());
                generateSortOption(sb, field.getName(), response);
                sb.append("</th>");
            }
        });
        renderTableHeader( sb, instance, clazz.getSuperclass(), response );
    }

    private void generateSortOption(StringBuilder sb, String name, PagedSortedListResponse<?> response) {
        String asc = response.getOrderByAsc().get(name);
        String desc = response.getOrderByDesc().get(name);
        if ( asc != null ) {
            sb.append( " <a href=\"" ).append( asc ).append( "\">^</a>");
        }
        if ( desc != null ) {
            sb.append( " <a href=\"").append(desc).append("\">v</a>");
        }

    }

    private void renderTableRow( final StringBuilder sb, Class clazz, final Object element ) {
        if ( clazz == Object.class ) return;
        iterateFields( clazz, element, new FieldIterator() {
            public void field(Field field) throws IllegalAccessException {
                sb.append("<td>").append(field.get(element)).append("</td>");
            }
        });
        renderTableRow(sb, clazz.getSuperclass(), element );
    }

    private void iterateFields( Class clazz, Object instance, FieldIterator callback ) {
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
        void field( Field field ) throws IllegalAccessException;
    }

    private void renderLinkable(StringBuilder sb, Linkable link) {
        sb.append("<li>");
        appendAnchor( sb, link.getHref(), link.getName(), false );
        sb.append( "</li>" );
    }


    /**
     * Generates an HTML form based on the method argument.
     * It will reflectively look at the argument for the method,
     * which has to be a single argument of DTO type, and
     * construct the form based on that
     *
     * @param method
     * @return html form getting the parameters needed for the method
     */
    protected String createForm( Method method, String httpMethod, Resource resource ) {
        List<Parameter> parameters = ReflectionUtil.parameterList(method, resource);
        Class<?>[] types = method.getParameterTypes();
        StringBuilder sb = new StringBuilder();
        sb.append( "<html><body><form name='generatedform' action='").append(method.getName()).
                append("' method='").append(httpMethod).append("' >" );

        for ( int i=0; i<parameters.size(); i++ ) {
            Parameter parameter = parameters.get(i);
            htmlForParameter("argument" + (i + 1), parameter.parameterCls(), sb, parameter.parameterCls().getSimpleName(), parameter.getTemplate());
        }
        return sb.append( "<input type='submit' /></form></body></html>" ).toString();
    }

    private void htmlForParameter(String legend, Class<?> dto, StringBuilder sb, String typeName, Object templateValue) {
        sb.append("<fieldset><legend>").append(legend).append("</legend>");
        if ( ReflectionUtil.basicTypes.contains( dto ) ) {
            // special case for text area
            if ( templateValue != null && templateValue instanceof String && ( ((String) templateValue).length()> 20)) {
                String value = (String) templateValue;
                int rows = lines( value ) + 2;
                sb.append(typeName).append(": <textarea rows='").append( rows ).append("' cols='80' name='");
                sb.append(legend).append("'>").append(value).append("</textarea></br>");
            } else {
                sb.append(typeName).append(": <input type='text' ");
                if ( templateValue != null && ReflectionUtil.basicTypes.contains( templateValue.getClass() )) {
                    sb.append(" value='").append(templateValue).append("' ");
                }
                sb.append("name='").append( legend ).append("'/></br>");
            }
        } else if ( List.class.isAssignableFrom( dto ) ) {
            // TODO create textarea and accept a comma separated list of values
        } else if ( Map.class.isAssignableFrom( dto ) ) {
            // TODO
        } else {
            // dto type
            htmlForComposite(dto.getSimpleName(), dto, sb, legend + "." + dto.getSimpleName(), templateValue);
        }

        sb.append("</fieldset>");
    }

    private void htmlForComposite(String legend, Class<?> dto, StringBuilder sb, String fieldPath, Object templateValue ) {
        sb.append("<fieldset><legend>").append(legend).append("</legend>");
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
                sb.append(name).append(": <input type='");
                sb.append( name.equals("password")? "password": "text" );
                if ( fieldValue != null ) {
                    sb.append("' value='").append(fieldValue);
                }
                sb.append("' name='").append( fieldPath ).append( "." ).append( name).append("'/></br>");
            } else if ( type.isEnum() ) {
                // TODO handle enums
                sb.append(name).append( ": <select name='").append( fieldPath ).append( "." ).append(name).append("'>");
                for ( Object o : type.getEnumConstants() ) {
                    sb.append( "<option value='").append(o).append("'>").append(o).append("</option>");
                }
                sb.append("</select></br>");
            } else {
                // for now assume DTO subtype
                // TODO List & Map, (any other???)
                htmlForComposite(name, type, sb, fieldPath + "." + name, fieldValue);
            }
        }
        sb.append("</fieldset>");
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
