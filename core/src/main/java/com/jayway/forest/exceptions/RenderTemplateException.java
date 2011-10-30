package com.jayway.forest.exceptions;

import com.jayway.forest.reflection.Capability;

public class RenderTemplateException extends AbstractHtmlException {


    private Capability capability;

    public RenderTemplateException( Capability capability, int code ) {
        super(code, "");
        this.capability = capability;
    }

    public Capability getCapability() {
        return capability;
    }
}
