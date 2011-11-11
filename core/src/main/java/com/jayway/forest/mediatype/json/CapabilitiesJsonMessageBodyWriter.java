package com.jayway.forest.mediatype.json;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.jayway.forest.reflection.Capabilities;
import com.jayway.forest.reflection.CapabilityReference;
import com.jayway.forest.reflection.impl.CapabilityLinkable;
import com.jayway.forest.roles.Linkable;

public class CapabilitiesJsonMessageBodyWriter extends JsonMessageBodyWriter<Capabilities> {

	public CapabilitiesJsonMessageBodyWriter(Charset charset) {
		super(Capabilities.class, charset);
	}

	@Override
	public void writeTo(Capabilities capabilities, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream out) throws IOException, WebApplicationException {
        OutputStreamWriter writer = new OutputStreamWriter( out, charset);
        writer.append("[");
        List<CapabilityReference> all = new LinkedList<CapabilityReference>();
        all.addAll( capabilities.getQueries() );
        all.addAll( capabilities.getCommands() );
        all.addAll( capabilities.getResources() );
        for (Linkable link : capabilities.getDiscoveredLinks()) {
            all.add(new CapabilityLinkable(link));
        }
        if ( capabilities.getIdResource() != null ) {
            all.add( capabilities.getIdResource());
        }
        if ( !all.isEmpty() ) {
            toMapEntries(all, writer);
        }
        writer.append("]");
        writer.flush();
	}

    private void toMapEntries(List<CapabilityReference> list, Writer results) throws IOException {
        boolean first = true;
        for (CapabilityReference method : list) {
            if ( !first ) results.append( ",\n");
            else first = false;
            appendMethod(results, method);
        }
    }
}
