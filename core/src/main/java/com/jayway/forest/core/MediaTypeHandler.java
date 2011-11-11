package com.jayway.forest.core;

import com.jayway.forest.exceptions.UnsupportedMediaTypeException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

// FIXME: this class should be removed and we should use MediaType directly

public class MediaTypeHandler {

    public static final String APPLICATION_JSON = "application/json";
    public static final String TEXT_HTML        = "text/html";
    public static final String FORM_URL_ENCODED = "application/x-www-form-urlencoded";
    public static final String APPLICATION_ATOM = "application/atom+xml";
    public static final String ENCODING         = "; charset=utf-8";

    private boolean contentTypeApplicationJSON;
    private boolean contentTypeFormUrlEncoded;

    private String accept;

    /**
     * Handler for the accept and content type headers.     *
     */
    public MediaTypeHandler( HttpServletRequest request, HttpServletResponse response ) {
        String acceptHeader = request.getHeader("Accept");
        String contentTypeHeader = request.getHeader("Content-Type");
        String format = request.getParameter("format");
        if ( format == null ) format = "";
        // accept defaults to JSON
        accept = APPLICATION_JSON;
        response.setHeader( "Content-Type", APPLICATION_JSON + ENCODING);
        if ( acceptHeader != null ) {
            if ( acceptHeader.contains(APPLICATION_JSON) ) {
                // if header contains json do nothing else
            } else if ( acceptHeader.contains(APPLICATION_ATOM) || format.equals("atom") ) {
                accept = APPLICATION_ATOM;
                response.setHeader( "Content-Type", APPLICATION_ATOM);
            } else if ( acceptHeader.contains(TEXT_HTML) ) {
                accept = TEXT_HTML;
                response.setHeader( "Content-Type", TEXT_HTML + ENCODING);
            }
        }

        if ( contentTypeHeader != null ) {
            if ( contentTypeHeader.contains( FORM_URL_ENCODED ) ) {
                contentTypeFormUrlEncoded = true;
                contentTypeApplicationJSON = false;
            } else if ( contentTypeHeader.contains( APPLICATION_JSON )) {
                contentTypeFormUrlEncoded = false;
                contentTypeApplicationJSON = true;
            } else {
                if ( !contentTypeHeader.isEmpty() ) throw new UnsupportedMediaTypeException();
                // default to JSON
                contentTypeApplicationJSON = true;
            }
        } else {
            contentTypeApplicationJSON = true;
        }
    }

    public MediaType accept() {
        return MediaType.valueOf(accept);
    }

    public boolean acceptHtml() {
        return accept.equals( TEXT_HTML );
    }

    public boolean contentTypeJSON() {
        return contentTypeApplicationJSON;
    }

    public boolean contentTypeFormUrlEncoded() {
        return contentTypeFormUrlEncoded;
    }
}
