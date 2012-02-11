package com.jayway.forest.roles;

import java.util.List;

import com.jayway.forest.hypermedia.Link;

/**
 */
public interface IdDiscoverableResource extends IdResource {
    List<? extends Link> discover();	// XXX: ? extends Link or just Link?!?!?!
}
