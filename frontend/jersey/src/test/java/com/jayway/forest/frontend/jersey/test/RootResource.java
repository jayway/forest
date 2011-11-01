package com.jayway.forest.frontend.jersey.test;

import com.jayway.forest.roles.Resource;

public class RootResource implements Resource {
	
	private static String storedText;

	public RootResource() {
	}
	
    public String add( Integer first, IntegerDTO second ) {
        return "" + (first + second.getInteger());
    }
    
    public StringDTO echo(String text) {
    	return new StringDTO(text);
    }
    
    public void save(String text) {
    	storedText = text;
    }

    public String load() {
    	return storedText;
    }

//    public Integer add( Integer first, IntegerDTO second ) {
//        return first + second.getInteger();
//    }
}
