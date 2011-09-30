package com.jayway.forest.core;

import com.jayway.forest.exceptions.UnsupportedMediaTypeException;

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
     * Handler for the accept and content type headers.
     *
     * Default value of the headers are application/json
     * @param acceptHeader
     * @param contentTypeHeader
     */
    public MediaTypeHandler( String acceptHeader, String contentTypeHeader ) {
        // TODO this is not working correctly, wildcards are not considered
        // accept defaults to JSON
        if ( acceptHeader != null ) {
            acceptApplicationJSON = true;
            if ( acceptHeader.contains(APPLICATION_JSON) ) {
                acceptApplicationJSON = true;
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
