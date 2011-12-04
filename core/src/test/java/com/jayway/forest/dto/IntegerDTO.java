package com.jayway.forest.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.ObjectMapper;

@XmlRootElement
public class IntegerDTO {

    private Integer integer;

//    public static IntegerDTO valueOf(String json) throws Exception {
//    	return new ObjectMapper().readValue(json, IntegerDTO.class);
//    }

    public IntegerDTO() {}
    public IntegerDTO( Integer integer ) {
        this.integer = integer;
    }

    public Integer getInteger() {
        return integer;
    }

    public void setInteger( Integer integer) {
        this.integer = integer;
    }

    @Override
    public String toString() {
        return ""+integer;
    }
}
