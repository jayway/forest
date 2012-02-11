package com.jayway.forest.roles;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class ReflectionUtil {
	public static Set<Class<?>> basicTypes;

	static {
		Set<Class<?>> basicTypes = new HashSet<Class<?>>();
        basicTypes.add( String.class);
        basicTypes.add( Long.class );
        basicTypes.add( Integer.class);
        basicTypes.add( Double.class );
        basicTypes.add( Float.class );
        basicTypes.add( Boolean.class );
        ReflectionUtil.basicTypes = Collections.unmodifiableSet(basicTypes);
    }
}
