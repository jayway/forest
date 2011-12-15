package com.jayway.forest.reflection;

import com.jayway.forest.core.MediaTypeHandler;
import com.jayway.forest.core.RoleManager;
import com.jayway.forest.exceptions.InternalServerErrorException;
import com.jayway.forest.roles.Resource;
import com.jayway.forest.roles.UriInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.Map;


public abstract class Capability implements CapabilityReference {
    protected static Logger log = LoggerFactory.getLogger(Capability.class);
	private final String name;
	private final String documentation;
    private final String href;
    private final String rel;
	
	public Capability(String name, String documentation, String rel) {
		this.name = name;
		this.documentation = documentation;
        this.href = RoleManager.role(UriInfo.class).getSelf() + name;
        this.rel = rel;
	}
	
	public boolean isDocumented() {
		return documentation != null;
	}
	public String documentation() {
		return documentation;
	}
	public String name() {
		return name;
	}

    public String uri() {
        return href;
    }

    public String rel() {
        return rel;
    }

    public abstract Object get(HttpServletRequest request);

    public abstract void put(Map<String, String[]> formParams, InputStream stream, MediaTypeHandler mediaTypeHandler);

	public abstract void post(Map<String, String[]> formParams, InputStream stream, MediaTypeHandler mediaTypeHandler );

	public abstract void delete();

	public abstract Resource subResource(String path);

    protected InternalServerErrorException internalServerError( Exception e ) {
        log.error( "Internal error", e );
        return new InternalServerErrorException();
    }

	public abstract String httpMethod();
}