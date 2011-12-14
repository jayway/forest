package com.jayway.forest.service;

import com.jayway.forest.dto.StringDTO;
import com.jayway.forest.dto.MyLinkable;
import com.jayway.forest.roles.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 */
public class ListResponseResource implements IdDiscoverableResource, ReadableResource<String> {


    public List<String> list() {
        List<String> list = new ArrayList<String>();
        list.add("world");
        list.add( "hello");
        return list;
    }

    public List<StringDTO> liststringdto() {
        List<StringDTO> list = new ArrayList<StringDTO>();
        list.add( new StringDTO("world"));
        list.add( new StringDTO("hello"));
        return list;
    }

    public List<StringDTO> listhowlong( Integer size ) {
        List<StringDTO> list = new ArrayList<StringDTO>(size);
        for ( int i=0; i<size; i++ ) {
            list.add( new StringDTO(""+i) );
        }
        return list;
    }

    public Iterable<StringDTO> immutableiterable( ) {
        List<StringDTO> list = new ArrayList<StringDTO>();
        list.add( new StringDTO("world"));
        list.add( new StringDTO("hello"));
        return Collections.unmodifiableList(list);
    }

    public Iterable<Linkable> linkables() {
        List<Linkable> list = new ArrayList<Linkable>(15);
        for (int i=0; i<15; i++) {
            list.add( new Linkable(""+i+"/", "number"+i));
        }
        return list;
    }

    public List<MyLinkable> testlinkables() {
        List<MyLinkable> list = new ArrayList<MyLinkable>(15);
        for (int i=0; i<15; i++) {
            list.add( new MyLinkable(""+i, "number"+i, "", "test"+i));
        }
        return list;
    }

    @Override
    public List<Linkable> discover() {
        return (List<Linkable>) linkables();
    }

    @Override
    public Resource id(String id) {
        return null;
    }

    @Override
    public String read() {
        return "Description text";
    }
}
