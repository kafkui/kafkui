package org.peekmoon.kafkui.tui;

 class StackItem {

    private final ScrollLayout scrollLayout;
    private final InnerLayout innerLayout;
    private final StackSizeMode mode;

    private final int value; // Size or proportion

    public StackItem(InnerLayout stackLayout, InnerLayout innerLayout, StackSizeMode mode, int value) {
        this.innerLayout = innerLayout;
        this.scrollLayout = new ScrollLayout("shl-" + innerLayout.getName(), innerLayout, HorizontalAlign.LEFT);
        scrollLayout.setParent(stackLayout);
        this.mode = mode;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void resize(int width, int height) {
        scrollLayout.resize(width, height);
    }

    public StackSizeMode getMode() {
        return mode;
    }

    public InnerLayout getScrollLayout() {
        return scrollLayout;
    }

    public int getWidth() {
        return scrollLayout.getWidth();
    }

    public int getHeight() {
        return scrollLayout.getHeight();
    }

    public int getContentWidth() {
        return innerLayout.getWidth();
    }

    public void adjustHeightToContent() {
        scrollLayout.setMinHeight(innerLayout.getHeight());
        scrollLayout.setMaxHeight(innerLayout.getHeight());
    }

     public void setMinWidth(int width) {
        scrollLayout.setMinWidth(width);
     }

     public void setMaxWidth(int width) {
        scrollLayout.setMaxWidth(width);
     }
 }
