package com.jayway.forest.legacy.exceptions;

import com.jayway.forest.legacy.roles.Linkable;

import javax.servlet.http.HttpServletResponse;

/**
 */
public class CreatedException extends AbstractHtmlException {
	private static final long serialVersionUID = 1;
    private Linkable linkable;

    public CreatedException(Linkable linkable) {
        super(HttpServletResponse.SC_CREATED, "Created");
        this.linkable = linkable;
    }

    public Linkable getLinkable() {
        return linkable;
    }


}
