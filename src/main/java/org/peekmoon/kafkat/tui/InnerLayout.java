package org.peekmoon.kafkat.tui;

import org.jline.utils.AttributedStringBuilder;

public abstract class InnerLayout implements Layout {

    private final String name;

    private Layout parent;

    InnerLayout(String name) {
        this.name = name;
    }

    @Override
    public void invalidate(boolean resizing) {
        if (parent != null) { // If no parent, we ignoring the event, no need to redraw
            parent.invalidate(resizing);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    protected void setParent(Layout parent) {
        if (this.parent != null && this.parent != parent) {
            throw new IllegalStateException("Reparenting is not supported");
        }
        this.parent = parent;
    }

    protected AttributedStringBuilder emptyLine() {
        return emptyLine(getWidth());
    }

    protected AttributedStringBuilder emptyLine(int width) {
        return new AttributedStringBuilder().append(" ".repeat(width));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + "@" + name + "  [" + getWidth() + "," + getHeight() + "]";
    }
}
