package com.jayway.forest.dto;

import java.util.Set;

public class WithIterable {

    private Set<IntegerDTO> integers;
    private Iterable<StringDTO> strings;

    public WithIterable() {}


    public Set<IntegerDTO> getIntegers() {
        return integers;
    }

    public Iterable<StringDTO> getStrings() {
        return strings;
    }

    public void setStrings(Iterable<StringDTO> strings) {
        this.strings = strings;
    }

    public void setIntegers(Set<IntegerDTO> integers) {
        this.integers = integers;
    }
}
