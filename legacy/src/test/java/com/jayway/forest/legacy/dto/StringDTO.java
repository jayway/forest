package com.jayway.forest.legacy.dto;


public class StringDTO {

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
