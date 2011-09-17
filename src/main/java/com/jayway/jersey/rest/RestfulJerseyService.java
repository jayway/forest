package com.jayway.jersey.rest;

import com.jayway.jersey.rest.resource.ContextMap;
import com.jayway.jersey.rest.resource.HtmlHelper;
import com.jayway.jersey.rest.resource.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.Providers;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Extend this class to define your rest
 * service
 */
public abstract class RestfulJerseyService {

    protected abstract Resource root();
    protected abstract void setupContext();

    private static ThreadLocal<ContextMap> currentMap = new ThreadLocal<ContextMap>();

    protected static void setContextMap( ContextMap map) {
        currentMap.set( map );
    }

    public static ContextMap getContextMap() {
        return currentMap.get();
    }

    @Context private UriInfo uriInfo;
    @Context private HttpServletResponse response;
    @Context private HttpServletRequest request;
    @Context private Providers providers;


    public static Set<Class> basicTypes;

    static {
        basicTypes = new HashSet<Class>();
        basicTypes.add( String.class);
        basicTypes.add( Long.class );
        basicTypes.add( Integer.class);
        basicTypes.add( Double.class );
        basicTypes.add( Boolean.class );
    }



    public void setup() {
        ContextMap contextMap = new ContextMap();
        contextMap.put( HttpServletResponse.class, response );
        contextMap.put( HttpServletRequest.class, request );
        contextMap.put( UriInfo.class, uriInfo );
        contextMap.put( HtmlHelper.class, new HtmlHelper() );
        contextMap.put( Providers.class, providers );
        setContextMap(contextMap);

        // call application specific context setup
        setupContext();
    }

    @GET
    @Produces( "text/html;charset=utf-8" )
    @Consumes( "text/html;charset=utf-8" )
    public Response capabilities() {
        setup();
        if ( uriInfo.getPath().endsWith( "/") ) {
            root().capabilitiesHtml();
            return Response.status(200).build();
        }
        else throw new WebApplicationException( Response.Status.NOT_FOUND );
    }

    @GET
    @Path("{get:.*}")
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Object get(  ) {
        setup();
        return evaluateGet( new PathAndMethod( uriInfo.getPath() ));
    }

    @GET
    @Path("{get:.*}")
    @Produces( "text/html;charset=utf-8" )
    public Response getHtml(  ) throws IOException {
        setup();
        evaluateGetHtml( new PathAndMethod( uriInfo.getPath() ) );
        return Response.status(200).build();
    }

    @POST
    @Path("{post:.*}")
    @Consumes( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response postJSON( InputStream json ) {
        setup();
        return evaluatePostPut( new PathAndMethod( uriInfo.getPath() ), json, null);
    }
    
    @PUT
    @Path("{put:.*}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response putJSON( InputStream json ) {
        setup();
        return evaluatePostPut( new PathAndMethod( uriInfo.getPath()), json, null );
    }


    @POST
    @Path("{post:.*}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response postForm( MultivaluedMap<String, String> formParams ) {
        setup();
        return evaluatePostPut( new PathAndMethod( uriInfo.getPath() ), null, formParams );
    }

    @PUT
    @Path("{put:.*}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response putForm( MultivaluedMap<String, String> formParams ) {
        setup();
        return evaluatePostPut( new PathAndMethod( uriInfo.getPath() ), null, formParams );
    }


    @DELETE
    @Path("{delete:.*}")
    public Response delete() {
        setup();
        evaluateDelete( new PathAndMethod( uriInfo.getPath() ));
        return Response.ok( "Operation Completed Successfully" ).status(200).build();
    }

    protected Object evaluateGet( PathAndMethod pathAndMethod ) {
        if ( pathAndMethod.method() == null ) {
            // TODO add support for discovery using XML and JSON
            evaluatePath( pathAndMethod.pathSegments() ).capabilitiesHtml();
            return null;
        }
        return evaluatePath( pathAndMethod.pathSegments() ).get(pathAndMethod.method());
    }

    protected void evaluateGetHtml( PathAndMethod pathAndMethod ) throws IOException {
        if ( pathAndMethod.method() == null ) {
            evaluatePath( pathAndMethod.pathSegments() ).capabilitiesHtml();
        } else {
            evaluatePath( pathAndMethod.pathSegments() ).getHtml(pathAndMethod.method());
        }
    }

    protected Response evaluatePostPut( PathAndMethod pathAndMethod, InputStream stream, MultivaluedMap<String, String> formParams ) {
        if ( pathAndMethod.method() == null ) {
            throw new WebApplicationException( HttpServletResponse.SC_METHOD_NOT_ALLOWED );
        }
        return evaluatePath( pathAndMethod.pathSegments() ).post(pathAndMethod.method(), formParams, stream);
    }

    protected void evaluateDelete( PathAndMethod pathAndMethod ) {
        if ( pathAndMethod.method() != null ) {
            throw new WebApplicationException( HttpServletResponse.SC_METHOD_NOT_ALLOWED );
        }
        evaluatePath( pathAndMethod.pathSegments() ).invokeDelete();
    }

    private Resource evaluatePath( List<String> segments ) {
        Resource current = root();
        for ( String pathSegment: segments ) {
            current = current.invokePathMethod(pathSegment);
        }
        return current;
    }

    class PathAndMethod {
        private List<String> pathSegments;
        private String method;

        public PathAndMethod( String rawPath, String method ) {
            this(rawPath);
            if ( this.method == null ) this.method = method;
        }

        public PathAndMethod( String rawPath ) {
            int index = rawPath.indexOf( '/' );
            if ( index > 0 ) {
                rawPath = rawPath.substring( index+1 );
            }
            boolean onlyPathSegments = rawPath.endsWith("/");
            pathSegments =  new ArrayList<String>();
            String[] split = rawPath.split("/");
            for ( int i=0; i<split.length; i++) {
                if ((onlyPathSegments || i != split.length - 1) && split[i].length() > 0) {
                    pathSegments.add(split[i]);
                }
            }
            method = null;
            if (!onlyPathSegments) {
                method = split[ split.length -1 ];
            }
        }

        public List<String> pathSegments() {
            return pathSegments;
        }

        public String method() {
            return method;
        }
    }
}
