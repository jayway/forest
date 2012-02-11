package com.jayway.forest.legacy.service;

import com.jayway.forest.legacy.dto.StringAndIntegerDTO;
import com.jayway.forest.legacy.roles.UpdatableResource;

public class UpdateResource implements UpdatableResource<StringAndIntegerDTO> {

    @Override
    public void update(StringAndIntegerDTO argument) {
        StateHolder.set( argument );
    }
}
