package com.jayway.forest.legacy.core;

import com.jayway.forest.legacy.di.DependencyInjectionSPI;
import com.jayway.forest.legacy.exceptions.MethodNotAllowedException;
import com.jayway.forest.legacy.reflection.Capabilities;
import com.jayway.forest.legacy.reflection.Capability;
import com.jayway.forest.legacy.reflection.impl.*;
import com.jayway.forest.legacy.roles.*;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class ForestCore {

	private final DependencyInjectionSPI dependencyInjectionSPI;
	private final ResourceUtil resourceUtil;
	private final Application application;

	public ForestCore(Application application, DependencyInjectionSPI dependencyInjectionSPI) {
		this.application = application;
		this.dependencyInjectionSPI = dependencyInjectionSPI;
		resourceUtil = new ResourceUtil(dependencyInjectionSPI);
	}

    private PathAndMethod setup(HttpServletRequest request) {
        String path = request.getPathInfo();
        RoleManager.spi = dependencyInjectionSPI;
        // call application specific context setup
        application.setupRequestContext();
        return new PathAndMethod(path );
    }

    public Object get(HttpServletRequest request) {
    	PathAndMethod pathAndMethod = setup(request);
        if ( pathAndMethod.method() == null ) {
            return capabilities(evaluatePath(pathAndMethod.pathSegments()), request);
        }
        return resourceUtil.get(request, evaluatePath(pathAndMethod.pathSegments()), pathAndMethod.method());
    }

    public void put(HttpServletRequest request, InputStream stream, Map<String, String[]> formParams, MediaTypeHandler mediaTypeHandler) {
    	PathAndMethod pathAndMethod = setup(request);
        Resource resource = evaluatePath(pathAndMethod.pathSegments());
        String methodName = methodName( resource, pathAndMethod.method(), request.getMethod()  ); 
        resourceUtil.put(resource, methodName, formParams, stream, mediaTypeHandler);
    }

    public void post(HttpServletRequest request, InputStream stream, Map<String, String[]> formParams, MediaTypeHandler mediaTypeHandler) {
    	PathAndMethod pathAndMethod = setup(request);
        Resource resource = evaluatePath(pathAndMethod.pathSegments());
        String methodName = methodName( resource, pathAndMethod.method(), request.getMethod()  );
        resourceUtil.post(resource, methodName, formParams, stream, mediaTypeHandler);
    }

    public void delete(HttpServletRequest request) {
    	PathAndMethod pathAndMethod = setup(request);
        Resource resource = evaluatePath(pathAndMethod.pathSegments());
        String methodName = methodName( resource, pathAndMethod.method(), request.getMethod()  );
        resourceUtil.delete(resource, methodName);
    }

    private String methodName(Resource resource, String methodName, String httpMethod ) {
        if ( methodName != null ) return methodName;
        if ( httpMethod.equals("PUT") && resource instanceof UpdatableResource ) {
            return "update";
        } else if ( httpMethod.equals("DELETE") && resource instanceof DeletableResource ) {
            return "delete";
        } else if ( httpMethod.equals("POST") && resource instanceof CreatableResource) {
            return "create";
        }
        throw new MethodNotAllowedException();
    }

    private Resource evaluatePath( List<String> segments ) {
        Resource current = dependencyInjectionSPI.postCreate(application.root());
        UriInfo uriInfo = dependencyInjectionSPI.getRequestContext(UriInfo.class);
        for ( String pathSegment: segments ) {
            current = resourceUtil.invokePathMethod( current, pathSegment );
            current = dependencyInjectionSPI.postCreate(current);
            uriInfo.addPath( pathSegment );
        }
        return current;
    }

    private Capabilities capabilities(Resource resource, HttpServletRequest request) {
        Class<?> clazz = resource.getClass();
        Capabilities capabilities = new Capabilities(clazz.getName());
        CapabilityQueryForList discoverMethod = null;
        for ( Method m : clazz.getDeclaredMethods() ) {
            if ( m.isSynthetic() ) continue;
            Capability capability = resourceUtil.createCapability(resource, m);
            if (capability instanceof CapabilityCommand) {
                capabilities.addCommand(capability);
            } else if (capability instanceof CapabilityQueryForList) {
                capabilities.addQuery(capability);
                if ( capability.name().equals( "discover") ) {
                    discoverMethod = (CapabilityQueryForList) capability;
                }
            } else if (capability instanceof CapabilityQuery) {
                capabilities.addQuery(capability);
            } else if (capability instanceof CapabilitySubResource) {
                capabilities.addResource(capability);
            } else if ( capability instanceof CapabilityIdResource ) {
                capabilities.addIdResource( capability );
            }
        }
        if ( resource instanceof IdDiscoverableResource) {
            if ( discoverMethod != null ) {
                try {
                    capabilities.setDiscovered( discoverMethod.get(request) );
                } catch( Exception e) {
                    // nothing discovered ignore
                }
            }
        }
        if (resource instanceof ReadableResource) {
            try {
                capabilities.setReadResult(((ReadableResource) resource).read());
            } catch ( Exception e) {
                capabilities.setReadResult("Exception occurred when evaluating 'read'");
            }
        }
        return capabilities;
    }

}
