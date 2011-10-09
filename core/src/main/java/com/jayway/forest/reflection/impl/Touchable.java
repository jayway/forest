package com.jayway.forest.reflection.impl;

/**
 */
public class Touchable {
    private boolean touched;
    public Touchable() {
        this.touched = false;
    }

    protected void touch() {
        touched = true;
    }

    public boolean isTouched() {
        return touched;
    }

}
