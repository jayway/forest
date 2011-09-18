package com.jayway.restfuljersey.samples.bank;

import com.jayway.restfuljersey.samples.bank.exceptions.NotFoundException;
import com.jayway.restfuljersey.samples.bank.exceptions.OverdrawException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 */
@Provider
public class RuntimeExceptionMapper implements
        ExceptionMapper<RuntimeException> {

    public Response toResponse(RuntimeException ex) {

        if ( ex instanceof WebApplicationException) {
            return ((WebApplicationException) ex).getResponse();
        } else if ( ex instanceof NotFoundException ) {
            return Response.status( Response.Status.NOT_FOUND ).build();
        } else if ( ex instanceof OverdrawException ) {
            return Response.status( Response.Status.CONFLICT ).build();
        }

        return Response.status( Response.Status.INTERNAL_SERVER_ERROR ).build();
    }
}