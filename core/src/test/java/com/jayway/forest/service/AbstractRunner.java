package com.jayway.forest.service;

import com.jayway.forest.core.JSONHelper;
import com.jayway.forest.grove.RoleManager;

import org.json.simple.JSONValue;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.mortbay.jetty.testing.ServletTester;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 */
public class AbstractRunner {

    private static ServletTester tester;
    private static String baseUrl;

    private Map<String, String> queryParams;

    @Before
    public void setup() {
        StateHolder.set(null);
        RestfulServletService.reset();
        queryParams = new LinkedHashMap<String, String>();
    }


    @BeforeClass
    public static void initServletContainer () throws Exception
    {
        tester = new ServletTester();
        tester.setContextPath("/");
        tester.addServlet( RestfulServletService.class, "/bank/*");
        baseUrl = tester.createSocketConnector(true);
        tester.start();
    }

    /**
     * Stops the Jetty container.
     */
    @AfterClass
    public static void cleanupServletContainer () throws Exception
    {
        tester.stop();
    }

    protected void post( String url, String json) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL( baseUrl + url).openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod( "POST" );
        conn.setRequestProperty("Content-Type", "application/json" );
        OutputStreamWriter writer = new OutputStreamWriter( conn.getOutputStream(), Charset.forName("UTF-8"));
        writer.write(json, 0, json.length());
        writer.flush();
        conn.getInputStream();
    }

    protected <T> T get( String url, Class<T> clazz ) throws IOException {
        return get( url, clazz, "application/json" );
    }

    protected <T> T get( String url, Class<T> clazz, String accept ) throws IOException {
        if ( queryParams.size() > 0 ) {
            StringBuilder sb = new StringBuilder( url ).append( "?");
            boolean first = true;
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                if ( !first ) sb.append("&");
                sb.append( entry.getKey() ).append( "=").append( entry.getValue() );
                first = false;
            }
            url = sb.toString();
        }
        URLConnection urlConnection = new URL(baseUrl + url).openConnection();
        urlConnection.setRequestProperty("Content-Type", "application/json" );
        urlConnection.setRequestProperty("Accept", accept );

        if ( accept.equals( "application/json") ) {
            Object parse = JSONValue.parse(new InputStreamReader(urlConnection.getInputStream()));
            if ( clazz == Object.class ) return (T) parse;
            return new JSONHelper().fromJSON( clazz, parse );
        } else {
            return (T) new BufferedReader( new InputStreamReader( urlConnection.getInputStream()) ).readLine();
        }
    }

    protected void queryParam( String key, String value ) {
        queryParams.put( key, value );
    }
}
