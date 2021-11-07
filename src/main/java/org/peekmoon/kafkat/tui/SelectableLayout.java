package org.peekmoon.kafkat.tui;

import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

public class SelectableLayout extends InnerLayout {

    private final InnerLayout inner;
    private Integer selectedOffset;

    public SelectableLayout(InnerLayout inner) {
        this.inner = inner;
        inner.setParent(this);
        this.selectedOffset = 3;
    }

    public int selectUp() {
        if (selectedOffset>0) {
            selectedOffset--;
            invalidate();
        }
        return selectedOffset;
    }

    public int selectDown() {
        if (selectedOffset < getHeight() -1) {
            selectedOffset++;
            invalidate();
        }
        return selectedOffset;
    }

    @Override
    public int getWidth() {
        return inner.getWidth();
    }

    @Override
    public int getHeight() {
        return inner.getHeight();
    }

    @Override
    public void resize(int width, int height) {
        inner.resize(width, height);
    }

    @Override
    public AttributedStringBuilder render(int y) {
        var innerLine = inner.render(y);
        if (y != selectedOffset) return innerLine;

        return new AttributedStringBuilder().append(innerLine, AttributedStyle.INVERSE);
    }

    public int getSelectedOffet() {
        return selectedOffset;
    }
}
