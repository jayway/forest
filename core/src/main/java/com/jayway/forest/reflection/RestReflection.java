package com.jayway.forest.reflection;

import java.lang.reflect.Method;


public interface RestReflection {
	Object renderCapabilities(Capabilities capabilities);
	Object renderCommandForm(Method method);
	Object renderQueryForm(Method method);
    Object renderListResponse(PagedSortedListResponse responseObject);
}
