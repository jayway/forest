package com.jayway.forest.reflection;

import com.jayway.forest.roles.Linkable;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public final class JsonRestReflection implements RestReflection {
	
	public static final RestReflection INSTANCE = new JsonRestReflection();
	
	private JsonRestReflection() {
	}

	@Override
	public Object renderCapabilities(Capabilities capabilities) {
        StringBuilder results = new StringBuilder( );
        results.append("{");
        List<ResourceMethod> all = new LinkedList<ResourceMethod>();
        all.addAll( capabilities.getQueries() );
        all.addAll( capabilities.getCommands() );
        all.addAll( capabilities.getResources() );
        for (Linkable link : capabilities.getDiscovered()) {
            all.add(new ResourceMethod(link));
        }
        if ( !all.isEmpty() ) {
            toMapEntries(all, results);
        }
        results.append("}");
		return results.toString();
	}

    private void appendMethod( StringBuilder sb, ResourceMethod method ) {
        sb.append( "\"" ).append( method.name() );
        if ( method.isSubResource() ) {
            sb.append( "/");
        }
        sb.append("\" : { \"method\" : ");
        if ( method.isCommand() ) {
            sb.append( "\"PUT\" }" );
        } else if ( method.isQuery() ) {
            sb.append( "\"GET\" }" );
        } else {
            // sub resource
            sb.append( "\"GET\" }" );
        }
    }

    private void toMapEntries(List<ResourceMethod> list, StringBuilder results) {
        boolean first = true;
        for (ResourceMethod method : list) {
            if ( !first ) results.append( ",");
            appendMethod( results, method );
            first = false;
        }
    }

	@Override
	public Object renderCommandForm(Method method) {
		return createForm(method, "POST");
	}

	@Override
	public Object renderQueryForm(Method method) {
		return createForm(method, "GET");
	}

    protected String createForm( Method method, String httpMethod ) {
    	return "N/A";
    }

}
