package com.jayway.forest.frontend.jersey;

import com.jayway.forest.core.Application;
import com.jayway.forest.core.ForestCore;
import com.jayway.forest.core.MediaTypeHandler;
import com.jayway.forest.di.DependencyInjectionSPI;
import com.jayway.forest.roles.UriInfo;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Is it possible to put @Consumes / @Produces on the Resource objects?

public class ForestJerseyService {
    private final ForestCore forest;
	private final DependencyInjectionSPI dependencyInjectionSPI;

	public ForestJerseyService(Application application, DependencyInjectionSPI dependencyInjectionSPI) {
		this.dependencyInjectionSPI = dependencyInjectionSPI;
		this.forest = new ForestCore(application, dependencyInjectionSPI);
	}

	// TODO: exceptions?
	
    @GET
    public Object capabilities(@Context HttpServletRequest request) {
    	prepare(request);
    	return forest.get(request);
    }

    @GET
    @Path("{get:.*}")
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "text/html;charset=utf-8" })
    public Object get(@Context HttpServletRequest request) {
    	prepare(request);
    	return forest.get(request);
    }
    
	@POST
    @Path("{post:.*}")
    @Consumes( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response postJSON(@Context HttpServletRequest request, InputStream stream ) {
    	prepare(request);
    	MediaTypeHandler mediaTypeHandler = null;	// FIXME!!!!
    	forest.post(request, stream, null, mediaTypeHandler);
    	return Response.ok().build();
    }
    
    @PUT
    @Path("{put:.*}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response putJSON( @Context HttpServletRequest request, InputStream stream) {
    	prepare(request);
    	MediaTypeHandler mediaTypeHandler = null;	// FIXME!!!!
    	forest.put(request, stream, null, mediaTypeHandler);
    	return Response.ok().build();
    }


    @POST
    @Path("{post:.*}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response postForm(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
    	prepare(request);
    	MediaTypeHandler mediaTypeHandler = null;	// FIXME!!!!
    	forest.post(request, null, toPlainMap(formParams), mediaTypeHandler);
    	return Response.ok().build();
    }

	@PUT
    @Path("{put:.*}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response putForm(@Context HttpServletRequest request, MultivaluedMap<String, String> formParams) {
    	prepare(request);
    	MediaTypeHandler mediaTypeHandler = null;	// FIXME!!!!
    	forest.put(request, null, toPlainMap(formParams), mediaTypeHandler);
    	return Response.ok().build();
    }


    @DELETE
    @Path("{delete:.*}")
    public Response delete(@Context HttpServletRequest request) {
    	prepare(request);
    	forest.delete(request);
    	return Response.ok("Operation Completed Successfully").build();
    }

    private Map<String, String[]> toPlainMap(MultivaluedMap<String, String> formParams) {
    	Map<String, String[]> plain = new HashMap<String, String[]>();
    	for (Map.Entry<String, List<String>> entry : formParams.entrySet()) {
			plain.put(entry.getKey(), entry.getValue().toArray(new String[entry.getValue().size()]));
		}
		return plain;
	}

    private void prepare(HttpServletRequest request) {
        dependencyInjectionSPI.addRequestContext( UriInfo.class, new UriInfo( request ) );
        dependencyInjectionSPI.addRequestContext(HttpServletRequest.class, request);
//        dependencyInjectionSPI.addRequestContext(HttpServletResponse.class, response);
	}
}
