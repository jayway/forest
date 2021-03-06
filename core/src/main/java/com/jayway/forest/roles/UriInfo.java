package com.jayway.forest.roles;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Convenience class for extracting various bits and pieces.
 *
 * @author Mads Enevoldsen
 *
 * TODO Provide getters for scheme, serverName, serverPort, contextPath, servletPath, pathInfo, and queryString
 */
public class UriInfo {
    private String baseUrl;
    private String relativeUrl;

    public UriInfo(HttpServletRequest request) {
        String url;
        try {
            final String encoding = request.getCharacterEncoding();
            url = URLDecoder.decode(request.getRequestURL().toString(), encoding != null ? encoding : "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        String path = request.getPathInfo();
        int urlLength = url.length();
        if (StringUtils.isNotEmpty(path)) {
            this.baseUrl = url.substring(0, urlLength - path.length() + 1);
        } else {
            this.baseUrl = url.substring(0, urlLength);
        }
        this.relativeUrl = "";
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getRelativeUrl() {
        return relativeUrl;
    }

    public String getSelf() {
        return baseUrl + relativeUrl;
    }

    public void addPath( String path ) {
        relativeUrl += path + '/';
    }
}
