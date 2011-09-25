package com.jayway.jersey.rest.reflection;

import java.lang.reflect.Method;


public interface RestReflection {
	Object renderCapabilities(Capabilities capabilities);
	Object renderCommandForm(Method method);
	Object renderQueryForm(Method method);
}
