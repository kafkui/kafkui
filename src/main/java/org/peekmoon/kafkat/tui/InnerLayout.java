package org.peekmoon.kafkat.tui;

import org.jline.utils.AttributedStringBuilder;

public abstract class InnerLayout implements Layout {

    private Layout parent;

    protected AttributedStringBuilder emptyLine(int width) {
        return new AttributedStringBuilder().append(" ".repeat(width));
    }

    @Override
    public void invalidate() {
        getParent().invalidate();
    }

    protected void setParent(Layout parent) {
        if (this.parent != null) {
            throw new IllegalStateException("Reparenting is not supported");
        }
        this.parent = parent;
    }

    private Layout getParent() {
        if (parent == null) {
            throw new IllegalStateException("Parent has not been initialized for " + this);
        }
        return parent;
    }

}
