package com.jayway.forest.samples.bank.grove.resources.accounts;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

import org.junit.Test;

import com.jayway.forest.core.MediaTypeHandler;


public class AccountResourceTest extends AbstractTestBase {

	@Test
	public void smokeTest() {
    	given().
			formParam("argument1", 100).
			contentType(MediaTypeHandler.FORM_URL_ENCODED).
		expect().
            statusCode(200).
            body(containsString("Operation completed successfully")).
        when().
            post("/accounts/11111/deposit");

	}

}
