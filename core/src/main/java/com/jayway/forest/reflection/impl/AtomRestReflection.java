package com.jayway.forest.reflection.impl;

import static com.jayway.forest.core.RoleManager.role;
import static org.apache.commons.lang.StringEscapeUtils.escapeXml;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.jayway.forest.exceptions.UnsupportedMediaTypeException;
import com.jayway.forest.reflection.RestReflection;
import com.jayway.forest.roles.UriInfo;
import com.jayway.forest.servlet.Response;

/**
 */
public class AtomRestReflection extends BasicRestReflection implements RestReflection {

	public static final AtomRestReflection INSTANCE = new AtomRestReflection(Charset.forName("UTF-8"));
    private VelocityEngine engine;

    private AtomRestReflection(Charset charset) {
    	super(charset);
    }

    @Override
    public void renderListResponse(OutputStream out, PagedSortedListResponse response) {

        engine = new VelocityEngine();
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

    @Override
    public void renderQueryResponse(OutputStream out, Object responseObject) {
    	// TODO: if the responseObject is ATOM it could be returned
        throw new UnsupportedMediaTypeException();
    }

    @Override
    public void renderError(OutputStream out, Response response) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter( out, charset);
        writer.write(response.message());
        writer.flush();
    }
}
