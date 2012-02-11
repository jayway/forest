package com.jayway.forest.legacy.mediatype.html;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.jayway.forest.legacy.reflection.impl.PagedSortedListResponse;
import com.jayway.forest.legacy.roles.Linkable;

public class PagedSortedListResponseHtmlMessageBodyWriter extends HtmlMessageBodyWriter<PagedSortedListResponse>{
	
	public PagedSortedListResponseHtmlMessageBodyWriter(Charset charset, String cssUrl) {
		super(PagedSortedListResponse.class, charset, cssUrl);
	}

	@Override
	public void writeTo(PagedSortedListResponse response, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream out) throws IOException, WebApplicationException {
        Writer writer = new OutputStreamWriter( out, charset);
        writeHeader(writer);
        writer.append( "<h1>").append(response.getName()).append("</h1>");

        List<?> list = response.getList();
        if ( !list.isEmpty() ) {
            appendPagingInfo( writer, response, false );
            if ( list.get(0).getClass() == Linkable.class ) {
                // render list
                if ( response.getOrderByAsc() != null && response.getOrderByDesc() != null ) {
                    appendAnchor( writer, (String)response.getOrderByAsc().get( "name"), "asc", false);
                    appendAnchor( writer, (String)response.getOrderByDesc().get( "name"), "desc", false);
                }
                writer.append( "<ul>" );
                for (Object elm : list) {
                    renderLinkable(writer, (Linkable) elm);
                }
                writer.append( "</ul>" );
            } else {
                // render table
                renderTable(writer, list, response );
            }
        }
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
                appendAnchor( writer, ((Linkable) elm).getUri(), ((Linkable) elm).getName(), false);
                writer.append("</td>");
                renderTableRow(writer, elm.getClass(), elm );
            } else {
                renderTableRow(writer, elm.getClass(), elm );
            }
            writer.append("</tr>");
        }
        writer.append("</table>");
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
        appendAnchor( writer, link.getUri(), link.getName(), false );
        writer.append( "</li>" );
    }

}
