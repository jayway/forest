package com.jayway.forest.service;

import com.jayway.forest.dto.StringAndIntegerDTO;
import com.jayway.forest.roles.UpdatableResource;

public class UpdateResource implements UpdatableResource<StringAndIntegerDTO> {

    @Override
    public void update(StringAndIntegerDTO argument) {
        StateHolder.set( argument );
    }
}
