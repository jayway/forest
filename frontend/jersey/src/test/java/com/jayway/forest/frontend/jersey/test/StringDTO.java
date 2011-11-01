package com.jayway.forest.frontend.jersey.test;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class StringDTO {

	@XmlElement
	private String string;

	public StringDTO() {}
	public StringDTO( String string ) {
		this.string = string;
	}

	public String string() {
		return string;
	}

	@Override
	public String toString() {
		return string;
	}
}
