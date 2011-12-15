package com.jayway.forest.mediatype.html;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.jayway.forest.core.JSONHelper;
import com.jayway.forest.reflection.Capabilities;
import com.jayway.forest.reflection.Capability;
import com.jayway.forest.reflection.impl.PagedSortedListResponse;
import com.jayway.forest.roles.Linkable;

public class CapabilitiesHtmlMessageBodyWriter extends HtmlMessageBodyWriter<Capabilities> {

	public CapabilitiesHtmlMessageBodyWriter(Charset charset, String cssUrl) {
		super(Capabilities.class, charset, cssUrl);
	}

	@Override
	public void writeTo(Capabilities capabilities, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream out) throws IOException, WebApplicationException {
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
                writer.append("<li><a href='").append(resource.getUri()).append("'>").append(resource.getName()).append("</a></li>");
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

        if (capabilities.getReadResult() != null ) {
            writer.append("<h2>Description</h2>");
            writer.append( new JSONHelper().toJSON( capabilities.getReadResult() ).toString());
        }
        writeFooter(writer);
        writer.flush();
	}

    private void appendMethod(Writer writer, Capability method ) throws IOException {
    	writer.append("<li><a href='").append(method.uri());
    	writer.append("'>").append( method.name() ).append("</a>");
        if ( method.isDocumented() ) {
        	writer.append(" <i>(").append( method.documentation() ).append("</i>)");
        }
        writer.append("</li>");
    }
}
