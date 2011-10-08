package com.jayway.forest.reflection;

import com.jayway.forest.core.JSONHelper;
import com.jayway.forest.roles.Linkable;

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
                results.append("<li><a href='").append(resource.href()).append("'>").append(resource.name()).append("</a></li>");
            }
            results.append("</ul>");
        }
        if ( capabilities.getPagedSortedListResponse() != null ) {
            PagedSortedListResponse response = capabilities.getPagedSortedListResponse();
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
        sb.append("<li><a href='").append( method.name() );
        if ( method instanceof SubResource ) {
            sb.append("/");
        }
        sb.append("'>").append( method.name() ).append("</a>");
        if ( method.isDocumented() ) {
            sb.append(" <i>(").append( method.documentation() ).append("</i>)");
        }
        sb.append("</li>");
    }

    @Override
    public Object renderCommandForm(Method method) {
        return createForm(method, "POST");
    }

    @Override
    public Object renderQueryForm(Method method) {
        return createForm(method, "GET");
    }

    private void appendPagingInfo( StringBuilder sb, PagedSortedListResponse response, boolean stripName ) {
        sb.append("Page ").append( response.getPage() ).append( " of ").append( response.getTotalPages() );

        appendAnchor( sb, response.getPrevious(), "previous", stripName );
        appendAnchor( sb, response.getNext(), "next" , stripName);
    }

    @Override
    public Object renderListResponse(PagedSortedListResponse response ) {
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
                appendAnchor( sb, ((Linkable) elm).href() + "/", ((Linkable) elm).href(), false);
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

    private void generateSortOption(StringBuilder sb, String name, PagedSortedListResponse response) {
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

    interface FieldIterator {
        void field( Field field ) throws IllegalAccessException;
    }

    private void renderLinkable(StringBuilder sb, Linkable link) {
        sb.append("<li>");
        appendAnchor( sb, link.href()+"/", link.name(), false );
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
    protected String createForm( Method method, String httpMethod ) {
        Class<?>[] types = method.getParameterTypes();
        StringBuilder sb = new StringBuilder();
        sb.append( "<html><body><form name='generatedform' action='").append(method.getName()).
                append("' method='").append(httpMethod).append("' >" );

        for ( int i=0; i<types.length; i++ ) {
            Class<?> type = types[i];
            create( "argument"+(i+1), type, sb, type.getSimpleName() );
        }
        /*for ( Class<?> type : types ) {
            createForm( type.getSimpleName(), type, sb, type.getSimpleName() );
        }*/
        return sb.append( "<input type='submit' /></form></body></html>" ).toString();
    }

    private void create( String legend, Class<?> dto, StringBuilder sb, String typeName ) {
        sb.append("<fieldset><legend>").append(legend).append("</legend>");
        if ( ReflectionUtil.basicTypes.contains( dto ) ) {
            sb.append(typeName).append(": <input type='text' ").
                    append("name='").append( legend ).append("'/></br>");
        } else if ( List.class.isAssignableFrom( dto ) ) {
            // TODO
        } else if ( Map.class.isAssignableFrom( dto ) ) {
            // TODO
        } else {
            // dto type
            createForm( dto.getSimpleName(), dto, sb, legend + "." + dto.getSimpleName() );
        }

        sb.append("</fieldset>");
    }

    private void createForm( String legend, Class<?> dto, StringBuilder sb, String fieldPath ) {
        sb.append("<fieldset><legend>").append(legend).append("</legend>");
        for ( Field f : dto.getDeclaredFields() ) {
            if ( Modifier.isFinal(f.getModifiers())) continue;
            String name = f.getName();
            Class<?> type = f.getType();
            // this must be one of the getters
            if (ReflectionUtil.basicTypes.contains(  type ) ) {
                sb.append(name).append(": <input type='").
                        append( name.equals("password")? "password": "text" ).
                        append("' name='").append( fieldPath ).append( "." ).append( name).append("'/></br>");
            } else if ( type.isEnum() ) {
                sb.append(name).append( ": <select name='").append( fieldPath ).append( "." ).append(name).append("'>");
                for ( Object o : type.getEnumConstants() ) {
                    sb.append( "<option value='").append(o).append("'>").append(o).append("</option>");
                }
                sb.append("</select></br>");
            } else {
                // for now assume DTO subtype
                // TODO List & Map, (any other???)
                createForm( name, type, sb, fieldPath + "." + name );
            }
        }
        sb.append("</fieldset>");
    }

}
