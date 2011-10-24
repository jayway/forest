package com.jayway.forest.reflection.impl;

import com.jayway.forest.exceptions.WrappedException;
import com.jayway.forest.roles.Linkable;
import com.jayway.forest.roles.Resource;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CreateCommandCapability extends CommandCapability {

	public CreateCommandCapability(Method method, Resource resource, String documentation, String rel) {
		super(method, resource, documentation, rel);
	}

    @Override
	public <T extends Resource> void invokeCommand(HttpServletResponse response, Object... arguments) {
        try {
            Linkable link = (Linkable) method.invoke(resource, arguments);
            response.addHeader( "Location", link.getHref() );
            response.setStatus(HttpServletResponse.SC_CREATED );
        } catch (InvocationTargetException e) {
            if ( e.getCause() instanceof RuntimeException ) {
                throw (RuntimeException) e.getCause();
            }
            throw new WrappedException( e.getCause() );
        } catch (IllegalAccessException e) {
            throw internalServerError( e );
        }
    }

}
