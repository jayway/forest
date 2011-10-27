package com.jayway.forest.reflection;

import com.jayway.forest.reflection.impl.BaseReflection;
import com.jayway.forest.reflection.impl.PagedSortedListResponse;
import com.jayway.forest.roles.Linkable;
import com.jayway.forest.roles.Resource;
import com.jayway.forest.servlet.Response;

import java.lang.reflect.Method;


public interface RestReflection {
	Object renderCapabilities(Capabilities capabilities);
    Object renderQueryForm(BaseReflection capability);
    Object renderCommandForm(BaseReflection capability);
    Object renderCommandCreateForm(BaseReflection capability);
    Object renderCommandDeleteForm(BaseReflection capability);
    Object renderListResponse(PagedSortedListResponse<?> responseObject);
    Object renderQueryResponse(Object responseObject);
    Object renderError( Response response );
    Object renderCreatedResponse(Linkable linkable);
}
