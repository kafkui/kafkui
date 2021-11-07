package org.peekmoon.kafkat.tui;

import org.jline.utils.AttributedStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrameLayout extends InnerLayout {

    private final static Logger log = LoggerFactory.getLogger(FrameLayout.class);


    private final InnerLayout inner;

    public FrameLayout(InnerLayout inner) {
        this.inner = inner;
        inner.setParent(this);
    }

    @Override
    public int getWidth() {
        return inner.getWidth() + 2;
    }

    @Override
    public int getHeight() {
        return inner.getHeight() + 2;
    }

    @Override
    public void resize(int width, int height) {
        inner.resize(width-2, height-2);
        log.debug("Resized : {}", this);
    }

    @Override
    public AttributedStringBuilder render(int y) {
        if (y == 0 || y == getHeight()-1) {
            return new AttributedStringBuilder().append("*".repeat(getWidth()));
        }
        return new AttributedStringBuilder().append("*").append(inner.render(y-1)).append("*");
    }

    @Override
    public String toString() {
        return super.toString() + " over [" + inner.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + "]";
    }
}
