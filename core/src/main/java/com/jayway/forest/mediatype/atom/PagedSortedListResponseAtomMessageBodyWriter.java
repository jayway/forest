package com.jayway.forest.mediatype.atom;

import static com.jayway.forest.core.RoleManager.role;
import static org.apache.commons.lang.StringEscapeUtils.escapeXml;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.jayway.forest.mediatype.AbstractMessageBodyWriter;
import com.jayway.forest.reflection.impl.PagedSortedListResponse;
import com.jayway.forest.roles.UriInfo;

public class PagedSortedListResponseAtomMessageBodyWriter extends AbstractMessageBodyWriter<PagedSortedListResponse>{
	
	private final Charset charset;

	public PagedSortedListResponseAtomMessageBodyWriter(Charset charset) {
		super(PagedSortedListResponse.class, MediaType.APPLICATION_ATOM_XML_TYPE);
		this.charset = charset;
	}
	
	@Override
	public void writeTo(PagedSortedListResponse response, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream out) throws IOException, WebApplicationException {
		VelocityEngine engine = new VelocityEngine();
        engine.setProperty("resource.loader","class");
        engine.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        try {
            engine.init();
            Template template = engine.getTemplate("com/jayway/forest/atom.vm", "utf-8");
            VelocityContext context = new VelocityContext();
            context.put( "base", role( UriInfo.class).getBaseUrl() );
            context.put( "title", response.getName() );
            context.put( "next", escapeXml(response.getNext()) );
            context.put( "previous", escapeXml(response.getPrevious()) );
            UriInfo uriInfo = role(UriInfo.class);
            context.put("self", escapeXml(uriInfo.getSelf()));
            context.put( "list", response.getList() );
            OutputStreamWriter writer = new OutputStreamWriter( out, charset);
            template.merge( context, writer );
            writer.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	}
}
