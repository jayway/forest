package com.jayway.forest.mediatype.json;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.jayway.forest.reflection.impl.PagedSortedListResponse;

public class PagedSortedListResponseJsonMessageBodyWriter extends JsonMessageBodyWriter<PagedSortedListResponse>{
	
	public PagedSortedListResponseJsonMessageBodyWriter(Charset charset) {
		super(PagedSortedListResponse.class, charset);
	}

	@Override
	public void writeTo(PagedSortedListResponse response, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream out) throws IOException, WebApplicationException {
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
}
