package com.jayway.forest.legacy.mediatype.html;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;

import javax.ws.rs.core.MediaType;

import com.jayway.forest.legacy.mediatype.AbstractMessageBodyWriter;
import com.jayway.forest.legacy.reflection.impl.PagedSortedListResponse;

public abstract class HtmlMessageBodyWriter<T> extends AbstractMessageBodyWriter<T> {
	
	protected final Charset charset;
	private final String cssUrl;

	public HtmlMessageBodyWriter(Class<T> clazz, Charset charset, String cssUrl) {
		super(clazz, MediaType.TEXT_HTML_TYPE);
		this.charset = charset;
		this.cssUrl = cssUrl;
	}
	
	protected void writeFooter(Writer writer) throws IOException {
		writer.append("</body></html>");
	}

	protected void writeHeader(Writer writer) throws IOException {
		writer.append( "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=").append(charset.name()).append("\">");
        if (cssUrl != null) {
        	writer.append("<link rel=\"stylesheet\" href=\"").append(cssUrl).append("\" type=\"text/css\" />");
        }
        writer.append("</head><body>" );
	}

    protected void appendPagingInfo( Writer writer, PagedSortedListResponse response, boolean stripName ) throws IOException {
        writer.append("Page ").append( response.getPage().toString() ).append( " of ").append( response.getTotalPages().toString() );

        appendAnchor( writer, response.getPrevious(), "previous", stripName );
        appendAnchor( writer, response.getNext(), "next" , stripName);
    }

    protected void appendAnchor(Writer writer, String id, String name, boolean stripMethodName ) throws IOException {
        if ( id == null ) return;
        if ( stripMethodName ) {
            id = id.substring( id.indexOf( "?" ) );
        }
        writer.append( "  <a href=\"" ).append( id ).append("\">").append(name).append("</a>");
    }
}
