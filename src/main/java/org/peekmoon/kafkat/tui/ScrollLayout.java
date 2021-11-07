package org.peekmoon.kafkat.tui;

import org.jline.utils.AttributedStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO : Managed Horizontal scrolling
public class ScrollLayout extends InnerLayout {

    private final static Logger log = LoggerFactory.getLogger(ScrollLayout.class);

    private final Layout virtualLayout;

    private int width, height;
    private int offsetX, offsetY;


    public ScrollLayout(InnerLayout virtualLayout) {
        this.offsetX = this.offsetY = 0;
        this.virtualLayout = virtualLayout;
        virtualLayout.setParent(this);
    }

    public void scrollUp() {
        offsetY++;
        invalidate();
    }

    public void scrollDown() {
        if (offsetY>0) {
            offsetY--;
            invalidate();
        }
    }


    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }


    @Override
    public void resize(int width, int height) {
        virtualLayout.resize(width, height);
        this.width = Math.min(virtualLayout.getWidth(), width);
        this.height = Math.min(virtualLayout.getHeight(), height);
        if (offsetY + this.height > virtualLayout.getHeight()) { // Stay inside when becoming bigger
            offsetY = virtualLayout.getHeight() - height;
        }
        log.debug("Resized : {}", this);
    }

    @Override
    public AttributedStringBuilder render(int y) {
        if (y + offsetY >= virtualLayout.getHeight()) {
            return emptyLine();
        }
        return virtualLayout.render(y + offsetY);
    }


    public void makeVisible(int offset) {
        if (offset < offsetY) {
            offsetY = offset;
            invalidate();
        }

        if (offset >= offsetY + height) {
            offsetY = offset - height + 1;
        }
    }
}
