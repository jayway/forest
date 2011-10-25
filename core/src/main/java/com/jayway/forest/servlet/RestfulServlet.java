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

public class RestfulServlet extends HttpServlet {
	private static final long serialVersionUID = 1;
	
	private ForestCore forest;
    private DependencyInjectionSPI dependencyInjectionSPI;

    public void initForest( Application application, DependencyInjectionSPI dependencyInjectionSPI){
        this.forest = new ForestCore(application, dependencyInjectionSPI);
        this.dependencyInjectionSPI = dependencyInjectionSPI;
    }

    /**
     * Override this to add custom exception mapping
     * @return
     */
    protected ExceptionMapper exceptionMapper() {
        return null;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        new ResponseHandler( req, resp, exceptionMapper(), dependencyInjectionSPI ).invoke(req, resp, new Runner() {
            public Object run(HttpServletRequest req, HttpServletResponse resp, MediaTypeHandler mediaType) throws IOException {
                return forest.evaluateGet(req);
            }
        });
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        new ResponseHandler( req, resp, exceptionMapper(), dependencyInjectionSPI  ).invoke(req, resp, new Runner() {
            @SuppressWarnings("unchecked")
            public Object run(HttpServletRequest req, HttpServletResponse resp, MediaTypeHandler mediaType) throws Exception {
                if (mediaType.contentTypeFormUrlEncoded()) {
                    forest.evaluatePostPut(req, null, req.getParameterMap(), mediaType );
                } else {
                    forest.evaluatePostPut(req, req.getInputStream(), null, mediaType);
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
        new ResponseHandler( req, resp, exceptionMapper(), dependencyInjectionSPI ).invoke(req, resp, new Runner() {
            public String run(HttpServletRequest req, HttpServletResponse resp, MediaTypeHandler mediaType) throws Exception {
                forest.evaluateDelete(req);
                return ResponseHandler.SUCCESS_RESPONSE;
            }
        });
    }

    public interface Runner {
        Object run( HttpServletRequest req, HttpServletResponse resp, MediaTypeHandler mediaTypeHandler ) throws Exception;
    }
}
