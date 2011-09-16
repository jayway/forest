package com.jayway.jersey.rest.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 */
@XmlRootElement( name = "integerholder" )
public class IntegerDTO {

    private Integer integer;

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
