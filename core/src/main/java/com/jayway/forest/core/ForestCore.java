package com.jayway.forest.core;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.jayway.forest.di.DependencyInjectionSPI;
import com.jayway.forest.exceptions.MethodNotAllowedException;
import com.jayway.forest.exceptions.MethodNotAllowedRenderTemplateException;
import com.jayway.forest.exceptions.NotFoundException;
import com.jayway.forest.reflection.*;
import com.jayway.forest.reflection.impl.CapabilityCommand;
import com.jayway.forest.reflection.impl.CapabilityQuery;
import com.jayway.forest.reflection.impl.CapabilitySubResource;
import com.jayway.forest.reflection.impl.CapabilityQueryForList;
import com.jayway.forest.roles.DescribedResource;
import com.jayway.forest.roles.IdDiscoverableResource;
import com.jayway.forest.roles.Resource;
import com.jayway.forest.roles.UriInfo;

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
        return new PathAndMethod(path, request.getMethod() );
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
        if ( pathAndMethod.method() == null ) {
            throw new MethodNotAllowedException();
        }
        resourceUtil.put(evaluatePath(pathAndMethod.pathSegments()), pathAndMethod.method(), formParams, stream, mediaTypeHandler);
    }

    public void post(HttpServletRequest request, InputStream stream, Map<String, String[]> formParams, MediaTypeHandler mediaTypeHandler) {
    	PathAndMethod pathAndMethod = setup(request);
        resourceUtil.post(evaluatePath(pathAndMethod.pathSegments()), pathAndMethod.method(), formParams, stream, mediaTypeHandler);
    }

    public void delete(HttpServletRequest request) {
    	PathAndMethod pathAndMethod = setup(request);
        resourceUtil.delete(evaluatePath(pathAndMethod.pathSegments()), pathAndMethod.method());
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
            Capability method = resourceUtil.createCapability(resource, m);
            if (method instanceof CapabilityCommand) {
                capabilities.addCommand(method);
            } else if (method instanceof CapabilityQueryForList) {
                capabilities.addQuery(method);
                if ( method.name().equals( "discover") ) {
                    discoverMethod = (CapabilityQueryForList) method;
                }
            } else if (method instanceof CapabilityQuery) {
                capabilities.addQuery(method);
            } else if (method instanceof CapabilitySubResource) {
                capabilities.addResource(method);
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
