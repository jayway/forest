package com.jayway.forest.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 */
@XmlRootElement( name = "integerholder" )
public class IntegerDTO {

    private Integer integer;

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
