package com.jayway.forest.legacy.mediatype.json;

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

import com.jayway.forest.legacy.reflection.Capabilities;
import com.jayway.forest.legacy.reflection.CapabilityReference;
import com.jayway.forest.legacy.reflection.impl.CapabilityLinkable;
import com.jayway.forest.legacy.roles.Linkable;

public class CapabilitiesJsonMessageBodyWriter extends JsonMessageBodyWriter<Capabilities> {

	public CapabilitiesJsonMessageBodyWriter(Charset charset) {
		super(Capabilities.class, charset);
	}

	@Override
	public void writeTo(Capabilities capabilities, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream out) throws IOException, WebApplicationException {
        OutputStreamWriter writer = new OutputStreamWriter( out, charset);
        writer.append("{ \"links\": [");
        List<CapabilityReference> links = new LinkedList<CapabilityReference>();
        links.addAll( capabilities.getQueries() );
        links.addAll( capabilities.getCommands() );
        links.addAll(capabilities.getResources());
        if ( !links.isEmpty() ) {
            toMapEntries(links, writer);
        }
        writer.append("]");

        links.clear();
        for (Linkable link : capabilities.getDiscoveredLinks()) {
            links.add(new CapabilityLinkable(link));
        }
        if ( !links.isEmpty() ) {
            writer.append(", \"discovered\": [");
            toMapEntries( links, writer );
            writer.append("]");
        }

        // do we need this???
        /*
        if ( capabilities.getIdResource() != null ) {
            links.add( capabilities.getIdResource());
        }
        */

        writer.append("}");
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
