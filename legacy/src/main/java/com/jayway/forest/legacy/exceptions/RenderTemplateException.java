package com.jayway.forest.legacy.exceptions;

import com.jayway.forest.legacy.reflection.Capability;

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
