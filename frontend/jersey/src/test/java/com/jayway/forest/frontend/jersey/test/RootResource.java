package com.jayway.forest.frontend.jersey.test;

import com.jayway.forest.roles.Resource;

public class RootResource implements Resource {
	
	public RootResource() {
	}
	
    public String add( Integer first, IntegerDTO second ) {
        return "" + (first + second.getInteger());
    }
//    public Integer add( Integer first, IntegerDTO second ) {
//        return first + second.getInteger();
//    }
}
