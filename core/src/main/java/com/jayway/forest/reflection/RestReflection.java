package com.jayway.forest.reflection;

import java.io.IOException;
import java.io.OutputStream;

import com.jayway.forest.reflection.impl.BaseReflection;
import com.jayway.forest.reflection.impl.PagedSortedListResponse;
import com.jayway.forest.roles.Linkable;
import com.jayway.forest.servlet.Response;

public interface RestReflection {
	void renderCapabilities(OutputStream out, Capabilities capabilities) throws IOException;
	void renderForm(OutputStream out, BaseReflection capability) throws IOException;
    void renderListResponse(OutputStream out, PagedSortedListResponse<?> responseObject) throws IOException;
    void renderQueryResponse(OutputStream out, Object responseObject) throws IOException;
    void renderError(OutputStream out, Response response) throws IOException;
    void renderCreatedResponse(OutputStream out, Linkable linkable) throws IOException;
}
