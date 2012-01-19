package com.jayway.forest.core;

import static com.jayway.restassured.RestAssured.expect;

import java.util.Iterator;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;

import com.jayway.forest.service.AbstractRunner;
import com.jayway.restassured.parsing.Parser;

@Ignore
public class AtomFeedTest extends AbstractRunner {

    @Test
    public void testAtomWithSuffix() {
        expect().
        	statusCode(200).
        	defaultParser(Parser.XML).
        	body("feed.title", Matchers.equalTo("listhowlong")).
        when().
    		get("/listresponse/listhowlong?argument1=7&pageSize=5&format=atom");
    }

    @Test
    public void testAtomWithContentType() throws Exception {
        expect().
        	statusCode(200).
        	contentType(MediaTypeHandler.APPLICATION_ATOM).
        	defaultParser(Parser.XML).
        	body(	"feed.title", Matchers.equalTo("listhowlong"), 
        			"feed.entry", hasLength(5)).
        when().
        	get("/listresponse/listhowlong?argument1=7&pageSize=5");
    }

    @SuppressWarnings("rawtypes")
	private static Matcher hasLength(final int length) {
    	return new BaseMatcher() {
			@Override
			public boolean matches(Object arg) {
				if (arg instanceof Iterable) {
					Iterator i = ((Iterable)arg).iterator();
					int size = 0;
					while (i.hasNext()) {
						size++;
						i.next();
					}
					return size == length;
				}
				return false;
			}

			@Override
			public void describeTo(Description desc) {
				desc.appendText("not correct length");
			}
		};
	}

	@Test
    public void testTemplateLinkable() {
        expect().
    		statusCode(200).
    		contentType(MediaTypeHandler.APPLICATION_ATOM).
    		defaultParser(Parser.XML).
    		body("feed.title", Matchers.equalTo("linkables")).
    	when().
    		get("/listresponse/linkables?format=atom&pageSize=5");
    }

}
