package com.jayway.forest.legacy.servlet;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import com.jayway.forest.legacy.core.Application;
import com.jayway.forest.legacy.core.ForestCore;
import com.jayway.forest.legacy.core.MediaTypeHandler;
import com.jayway.forest.legacy.di.DependencyInjectionSPI;
import com.jayway.forest.legacy.mediatype.SimpleErrorMessageBodyWriter;
import com.jayway.forest.legacy.mediatype.atom.PagedSortedListResponseAtomMessageBodyWriter;
import com.jayway.forest.legacy.mediatype.html.CapabilitiesHtmlMessageBodyWriter;
import com.jayway.forest.legacy.mediatype.html.ErrorHtmlMessageBodyWriter;
import com.jayway.forest.legacy.mediatype.html.FormHtmlMessageBodyWriter;
import com.jayway.forest.legacy.mediatype.html.LinkableHtmlMessageBodyWriter;
import com.jayway.forest.legacy.mediatype.html.PagedSortedListResponseHtmlMessageBodyWriter;
import com.jayway.forest.legacy.mediatype.html.QueryHtmlMessageBodyWriter;
import com.jayway.forest.legacy.mediatype.json.CapabilitiesJsonMessageBodyWriter;
import com.jayway.forest.legacy.mediatype.json.FormJsonMessageBodyWriter;
import com.jayway.forest.legacy.mediatype.json.LinkableJsonMessageBodyWriter;
import com.jayway.forest.legacy.mediatype.json.PagedSortedListResponseJsonMessageBodyWriter;
import com.jayway.forest.legacy.mediatype.json.QueryJsonMessageBodyWriter;

public class RestfulServlet extends HttpServlet {
	private static final long serialVersionUID = 1;
	
	private ForestCore forest;
    private DependencyInjectionSPI dependencyInjectionSPI;
    private MediaTypeHandlerContainer mediaTypeHandlerContainer = new MediaTypeHandlerContainer();
    
    public void initForest( Application application, DependencyInjectionSPI dependencyInjectionSPI){
        this.forest = new ForestCore(application, dependencyInjectionSPI);
        this.dependencyInjectionSPI = dependencyInjectionSPI;
        Charset charset = Charset.forName("UTF-8");
        mediaTypeHandlerContainer.addHandler(new CapabilitiesHtmlMessageBodyWriter(charset, getCssUrl()));
        mediaTypeHandlerContainer.addHandler(new ErrorHtmlMessageBodyWriter(charset, getCssUrl()));
        mediaTypeHandlerContainer.addHandler(new FormHtmlMessageBodyWriter(charset, getCssUrl()));
        mediaTypeHandlerContainer.addHandler(new LinkableHtmlMessageBodyWriter(charset, getCssUrl()));
        mediaTypeHandlerContainer.addHandler(new PagedSortedListResponseHtmlMessageBodyWriter(charset, getCssUrl()));
        
        mediaTypeHandlerContainer.addHandler(new CapabilitiesJsonMessageBodyWriter(charset));
        mediaTypeHandlerContainer.addHandler(new FormJsonMessageBodyWriter(charset));
        mediaTypeHandlerContainer.addHandler(new LinkableJsonMessageBodyWriter(charset));
        mediaTypeHandlerContainer.addHandler(new PagedSortedListResponseJsonMessageBodyWriter(charset));

        mediaTypeHandlerContainer.addHandler(new PagedSortedListResponseAtomMessageBodyWriter(charset));
        
        mediaTypeHandlerContainer.addHandler(new SimpleErrorMessageBodyWriter(MediaType.WILDCARD_TYPE, charset));

        mediaTypeHandlerContainer.addHandler(new QueryHtmlMessageBodyWriter(charset, getCssUrl()));
        mediaTypeHandlerContainer.addHandler(new QueryJsonMessageBodyWriter(charset));
    }

    protected String getCssUrl() {
		return null;
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
