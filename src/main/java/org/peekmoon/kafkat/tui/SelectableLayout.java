package org.peekmoon.kafkat.tui;

import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelectableLayout extends InnerLayout {

    private final static Logger log = LoggerFactory.getLogger(SelectableLayout.class);

    private final InnerLayout inner;
    private int selectedOffset;

    public SelectableLayout(InnerLayout inner) {
        super("selectable-" + inner.getName());
        this.inner = inner;
        inner.setParent(this);
    }

    public int selectUp() {
        if (selectedOffset>0) {
            selectedOffset--;
            log.info("Selecting up to {}", selectedOffset);
            invalidate();
        }
        return selectedOffset;
    }

    public int selectDown() {
        if (selectedOffset < getHeight() -1) {
            selectedOffset++;
            log.info("Selecting down to {}", selectedOffset);
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
        log.debug("Resizing : {} to {},{}", this, width, height);
        inner.resize(width, height);
        log.debug("Resized  : {}", this);
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
