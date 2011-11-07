package com.jayway.forest.service;

import com.jayway.forest.dto.IntegerDTO;
import com.jayway.forest.dto.StringDTO;
import com.jayway.forest.dto.WithIterable;
import com.jayway.forest.roles.Resource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 */
public class TypesResource implements Resource {

    public Float getfloat() {
        return 3.9f;
    }

    public void postfloat( Float f ) {
        StateHolder.set( f );
    }

    public WithIterable iterable() {
        WithIterable value = new WithIterable();
        final ArrayList<StringDTO> dtos = new ArrayList<StringDTO>();
        dtos.add( new StringDTO("STRING"));

        Iterable<StringDTO> strings = new Iterable<StringDTO>() {
            @Override
            public Iterator<StringDTO> iterator() {
                return dtos.iterator();
            }
        };

        Set<IntegerDTO> integers = new HashSet<IntegerDTO>();
        integers.add( new IntegerDTO(42 ));

        value.setStrings( strings );
        value.setIntegers(integers);
        return value;
    }

}
