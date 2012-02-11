package com.jayway.forest.legacy.roles;

import java.util.List;

/**
 */
public interface IdDiscoverableResource extends IdResource {
    List<? extends Linkable> discover();
}
