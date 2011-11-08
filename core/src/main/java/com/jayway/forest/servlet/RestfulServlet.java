package com.jayway.forest.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jayway.forest.core.Application;
import com.jayway.forest.core.ForestCore;
import com.jayway.forest.core.MediaTypeHandler;
import com.jayway.forest.di.DependencyInjectionSPI;
import com.jayway.forest.reflection.RestReflection;
import com.jayway.forest.reflection.impl.AtomRestReflection;
import com.jayway.forest.reflection.impl.HtmlRestReflection;
import com.jayway.forest.reflection.impl.JsonRestReflection;

public class RestfulServlet extends HttpServlet {
	private static final long serialVersionUID = 1;
	
	private ForestCore forest;
    private DependencyInjectionSPI dependencyInjectionSPI;
    private MediaTypeHandlerContainer mediaTypeHandlerContainer = new MediaTypeHandlerContainer();
    
    public void setHandler(String mediaType, RestReflection restReflection) {
    	mediaTypeHandlerContainer.setHandler(mediaType, restReflection);
	}

    public void initForest( Application application, DependencyInjectionSPI dependencyInjectionSPI){
        this.forest = new ForestCore(application, dependencyInjectionSPI);
        this.dependencyInjectionSPI = dependencyInjectionSPI;
    	mediaTypeHandlerContainer.setHandler(MediaTypeHandler.APPLICATION_JSON, JsonRestReflection.INSTANCE);
    	mediaTypeHandlerContainer.setHandler(MediaTypeHandler.TEXT_HTML, HtmlRestReflection.DEFAULT);
    	mediaTypeHandlerContainer.setHandler(MediaTypeHandler.APPLICATION_ATOM, AtomRestReflection.INSTANCE);
    }

    /**
     * Override this to add custom exception mapping
     * @return
     */
    protected ExceptionMapper exceptionMapper() {
        return null;
    }

    @Override
    protected void doGet(final HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        new ResponseHandler( req, resp, mediaTypeHandlerContainer, exceptionMapper(), dependencyInjectionSPI ).invoke( new Runner() {
            public Object run(MediaTypeHandler mediaType) {
                return forest.get(req);
            }
        });
    }

    @Override
    protected void doPost(final HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        new ResponseHandler( req, resp, mediaTypeHandlerContainer, exceptionMapper(), dependencyInjectionSPI  ).invoke(new Runner() {
            @SuppressWarnings("unchecked")
            public Object run(MediaTypeHandler mediaType) throws IOException {
                if (mediaType.contentTypeFormUrlEncoded()) {
                    forest.post(req, null, req.getParameterMap(), mediaType);
                } else {
                    forest.post(req, req.getInputStream(), null, mediaType);
                }
                return ResponseHandler.SUCCESS_RESPONSE;
            }
        });
    }

    @Override
    protected void doPut(final HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        new ResponseHandler( req, resp, mediaTypeHandlerContainer, exceptionMapper(), dependencyInjectionSPI  ).invoke(new Runner() {
            @SuppressWarnings("unchecked")
            public Object run(MediaTypeHandler mediaType) throws IOException {
                if (mediaType.contentTypeFormUrlEncoded()) {
                    forest.put(req, null, req.getParameterMap(), mediaType);
                } else {
                    forest.put(req, req.getInputStream(), null, mediaType);
                }
                return ResponseHandler.SUCCESS_RESPONSE;
            }
        });
    }

    @Override
    protected void doDelete(final HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        new ResponseHandler( req, resp, mediaTypeHandlerContainer, exceptionMapper(), dependencyInjectionSPI ).invoke(new Runner() {
            public String run( MediaTypeHandler mediaType) {
                forest.delete(req);
                return ResponseHandler.SUCCESS_RESPONSE;
            }
        });
    }

    public interface Runner {
        Object run( MediaTypeHandler mediaTypeHandler ) throws IOException;
    }
}
