package org.peekmoon.kafkat.tui;

import org.jline.utils.AttributedStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO : Check if it should be a frame layout
public class RootLayout implements Layout {

    private final static Logger log = LoggerFactory.getLogger(RootLayout.class);

    private final Display display;
    private final InnerLayout inner;

    public RootLayout(Display display, InnerLayout inner) {
        this.display = display;
        this.inner = inner;
        inner.setParent(this);
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
        log.debug("Resized : {}", this);
    }

    @Override
    public AttributedStringBuilder render(int y) {
        return inner.render(y);
    }

    @Override
    public void invalidate() {
        display.invalidate();
    }
}
