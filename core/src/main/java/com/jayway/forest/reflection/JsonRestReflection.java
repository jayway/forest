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
        List<CapabilityReference> all = new LinkedList<CapabilityReference>();
        all.addAll( capabilities.getQueries() );
        all.addAll( capabilities.getCommands() );
        all.addAll( capabilities.getResources() );
        // todo append the whole paging result
        for (Linkable link : capabilities.getDiscoveredLinks()) {
            all.add(new LinkCapabilityReference(link));
        }
        if ( !all.isEmpty() ) {
            toMapEntries(all, results);
        }
        results.append("}");
		return results.toString();
	}

    private void appendMethod( StringBuilder sb, CapabilityReference method ) {
        sb.append( "\"" ).append( method.name() );
        if ( method instanceof SubResource ) {
            sb.append( "/");
        }
        sb.append("\" : { \"method\" : \"");
        sb.append(method.httpMethod());
        sb.append("\" }");
    }

    private void toMapEntries(List<CapabilityReference> list, StringBuilder results) {
        boolean first = true;
        for (CapabilityReference method : list) {
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

    @Override
    public Object renderListResponse(PagedSortedListResponse responseObject) {
        // TODO
        return null;
    }

    protected String createForm( Method method, String httpMethod ) {
    	return "N/A";
    }

}
