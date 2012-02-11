package com.jayway.forest.legacy.service;

/**
 */
public class StateHolder {

    private static Object state;

    public static Object get() {
        return state;
    }

    public static void set( Object state ) {
        StateHolder.state = state;
    }


}
