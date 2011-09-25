package com.jayway.jersey.rest;

import com.jayway.jersey.rest.exceptions.*;
import com.jayway.jersey.rest.reflection.Capabilities;
import com.jayway.jersey.rest.resource.*;
import com.jayway.jersey.rest.roles.DescribedResource;
import com.jayway.jersey.rest.roles.IdDiscoverableResource;
import com.jayway.jersey.rest.roles.Linkable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.*;

public abstract class RestfulServlet extends HttpServlet {

    protected abstract Resource root();
    protected abstract void setupContext();
    protected abstract String servletMapping();

    /**
     * Override this to add custom exception mapping
     * @return
     */
    protected ExceptionMapper exceptionMapper() {
        return null;
    }

    private static ThreadLocal<ContextMap> map = new ThreadLocal<ContextMap>();

    protected static void setContextMap( ContextMap map) {
        RestfulServlet.map.set(map);
    }

    public static ContextMap getContextMap() {
        return map.get();
    }

    public static Set<Class> basicTypes;

    static {
        basicTypes = new HashSet<Class>();
        basicTypes.add( String.class);
        basicTypes.add( Long.class );
        basicTypes.add( Integer.class);
        basicTypes.add( Double.class );
        basicTypes.add( Boolean.class );
    }


    private String setup(HttpServletRequest req, HttpServletResponse resp) {
        String path = req.getRequestURI().substring( servletMapping().length() );
        if ( !path.startsWith( "/") ) {
            throw new NotFoundException();
        }
        ContextMap contextMap = new ContextMap();
        contextMap.put( HttpServletRequest.class, req );
        contextMap.put( HttpServletResponse.class, resp );
        setContextMap(contextMap);

        // call application specific context setup
        setupContext();
        return path;
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        new ResponseHandler( req, resp, exceptionMapper() ).invoke(req, resp, new Runner() {
            public Object run(HttpServletRequest req, HttpServletResponse resp, MediaTypeHandler mediaType) throws IOException {
                String path = setup(req, resp);
                return evaluateGet(new PathAndMethod(path));
            }
        });
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        new ResponseHandler( req, resp, exceptionMapper() ).invoke(req, resp, new Runner() {
            public Object run(HttpServletRequest req, HttpServletResponse resp, MediaTypeHandler mediaType) throws Exception {
                String path = setup(req, resp);
                if ( mediaType.contentTypeFormUrlEncoded() ) {
                    evaluatePostPut(new PathAndMethod(path), null, req.getParameterMap(), mediaType );
                } else {
                    evaluatePostPut(new PathAndMethod(path), req.getInputStream(), null, mediaType );
                }
                return ResponseHandler.SUCCESS_RESPONSE;
            }
        });
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost( req, resp );
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        new ResponseHandler( req, resp, exceptionMapper() ).invoke(req, resp, new Runner() {
            public String run(HttpServletRequest req, HttpServletResponse resp, MediaTypeHandler mediaType) throws Exception {
                String path = setup(req, resp);
                evaluateDelete(new PathAndMethod(path));
                return ResponseHandler.SUCCESS_RESPONSE;
            }
        });
    }

    public interface Runner {
        Object run( HttpServletRequest req, HttpServletResponse resp, MediaTypeHandler mediaTypeHandler ) throws Exception;
    }

    private Object evaluateGet( PathAndMethod pathAndMethod ) {
        if ( pathAndMethod.method() == null ) {
            return capabilities(evaluatePath(pathAndMethod.pathSegments()));
        }
        return ResourceUtil.get(evaluatePath(pathAndMethod.pathSegments()), pathAndMethod.method());
    }

    private void evaluatePostPut( PathAndMethod pathAndMethod, InputStream stream, Map<String, String[]> formParams, MediaTypeHandler mediaTypeHandler ) {
        if ( pathAndMethod.method() == null ) {
            // TODO allow post if CreatableResource
            throw new MethodNotAllowedException();
        }
        ResourceUtil.post(evaluatePath(pathAndMethod.pathSegments()), pathAndMethod.method(), formParams, stream, mediaTypeHandler);
    }

    private void evaluateDelete( PathAndMethod pathAndMethod ) {
        if ( pathAndMethod.method() != null ) {
            throw new MethodNotAllowedException();
        }
        ResourceUtil.invokeDelete(evaluatePath(pathAndMethod.pathSegments()));
    }

    private Resource evaluatePath( List<String> segments ) {
        Resource current = root();
        for ( String pathSegment: segments ) {
            current = ResourceUtil.invokePathMethod( current, pathSegment );
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

    private Capabilities capabilities(Resource resource) {
        Class<?> clazz = resource.getClass();
        Capabilities capabilities = new Capabilities(clazz.getName());
        for ( Method m : clazz.getDeclaredMethods() ) {
            if ( m.isSynthetic() ) continue;
            ResourceMethod method = new ResourceMethod( m );
            switch (method.type()) {
                case COMMAND:
                    capabilities.addCommand(method);
                    break;
                case QUERY:
                    capabilities.addQuery(method);
                    break;
                case SUBRESOURCE:
                    capabilities.addResource(method);
                    break;
            }
        }
        if ( resource instanceof IdDiscoverableResource) {
            try {
                capabilities.setDiscovered(  ((IdDiscoverableResource) resource).discover() );
            } catch( Exception e) {
                // nothing discovered ignore
            }
        }
        if (resource instanceof DescribedResource) {
            try {
                capabilities.setDescriptionResult(((DescribedResource) resource).description());
            } catch ( Exception e) {
                capabilities.setDescriptionResult("Exception occurred when evaluating 'description'");
            }
        }
        return capabilities;
    }
}
