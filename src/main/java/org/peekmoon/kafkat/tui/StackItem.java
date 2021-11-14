package org.peekmoon.kafkat.tui;


public class StackItem {

    private final InnerLayout inner;
    private final StackSizeMode mode;

    private final int value; // Size or proportion

    public StackItem(InnerLayout inner, StackSizeMode mode, int value) {
        this.inner = inner;
        this.mode = mode;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void resize(int width, int height) {
        inner.resize(width, height);
    }

    public StackSizeMode getMode() {
        return mode;
    }

    public InnerLayout getInner() {
        return inner;
    }


}
