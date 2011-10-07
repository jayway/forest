package com.jayway.forest.core;

import com.jayway.forest.exceptions.UnsupportedMediaTypeException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 */
public class MediaTypeHandler {

    public static final String APPLICATION_JSON = "application/json";
    public static final String TEXT_HTML = "text/html";
    public static final String FORM_URL_ENCODED = "application/x-www-form-urlencoded";

    private boolean acceptApplicationJSON;
    private boolean contentTypeApplicationJSON;
    private boolean contentTypeFormUrlEncoded;

    /**
     * Handler for the accept and content type headers.     *
     */
    public MediaTypeHandler( HttpServletRequest request, HttpServletResponse response ) {
        //request.getHeader("Accept"), request.getHeader("Content-Type"));
        String acceptHeader = request.getHeader("Accept");
        String contentTypeHeader = request.getHeader("Content-Type");
        // TODO this is not working correctly, wildcards are not considered
        // accept defaults to JSON
        if ( acceptHeader != null ) {
            acceptApplicationJSON = true;
            if ( acceptHeader.contains(APPLICATION_JSON) ) {
                acceptApplicationJSON = true;
                response.setHeader( "Content-Type", APPLICATION_JSON);
            } else {
                acceptApplicationJSON = !acceptHeader.contains(TEXT_HTML);
            }
        }

        if ( contentTypeHeader != null ) {
            if ( contentTypeHeader.equals( FORM_URL_ENCODED ) ) {
                contentTypeFormUrlEncoded = true;
                contentTypeApplicationJSON = false;
            } else if ( contentTypeHeader.equals( APPLICATION_JSON )) {
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


    public boolean acceptJSSON() {
        return acceptApplicationJSON;
    }

    public boolean contentTypeJSON() {
        return contentTypeApplicationJSON;
    }

    public boolean contentTypeFormUrlEncoded() {
        return contentTypeFormUrlEncoded;
    }
}
