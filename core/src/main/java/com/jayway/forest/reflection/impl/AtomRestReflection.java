package com.jayway.forest.reflection.impl;

import com.jayway.forest.exceptions.UnsupportedMediaTypeException;
import com.jayway.forest.reflection.Capabilities;
import com.jayway.forest.reflection.RestReflection;
import com.jayway.forest.roles.Resource;
import com.jayway.forest.roles.UriInfo;
import com.jayway.forest.servlet.Response;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.jayway.forest.core.RoleManager.role;

/**
 */
public class AtomRestReflection implements RestReflection {

    public static final AtomRestReflection INSTANCE = new AtomRestReflection();
    private VelocityEngine engine;

    private AtomRestReflection() {}

    @Override
    public Object renderListResponse(PagedSortedListResponse response) {
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
            context.put( "next", response.getNext() );
            context.put( "previous", response.getPrevious() );
            UriInfo uriInfo = role(UriInfo.class);
            context.put("self", uriInfo.getSelf());
            context.put( "list", response.getList() );
            StringWriter writer = new StringWriter();
            template.merge( context, writer );
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object renderQueryResponse(Object responseObject) {
        throw new UnsupportedMediaTypeException();
    }

    @Override
    public Object renderError(Response response) {
        return response.message();
    }

    @Override
    public Object renderCapabilities(Capabilities capabilities) {
        throw new UnsupportedMediaTypeException();
    }

    @Override
    public Object renderCommandForm(Method method, Resource resource ) {
        throw new UnsupportedMediaTypeException();
    }

    @Override
    public Object renderQueryForm(Method method, Resource resource ) {
        throw new UnsupportedMediaTypeException();
    }

}
