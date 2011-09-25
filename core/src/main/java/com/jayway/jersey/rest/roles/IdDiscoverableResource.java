package com.jayway.jersey.rest.roles;

import java.util.List;

/**
 */
public interface IdDiscoverableResource extends IdResource {
    List<Linkable> discover();
}
