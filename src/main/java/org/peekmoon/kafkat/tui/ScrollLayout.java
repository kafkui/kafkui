package org.peekmoon.kafkat.tui;

import org.jline.utils.AttributedStringBuilder;

//TODO : Managed Horizontal scrolling
public class ScrollLayout extends InnerLayout {

    private final Layout virtualLayout;

    private int width, height;
    private int offsetX, offsetY;
    private AttributedStringBuilder emptyLine;


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
        this.width = width; //Math.min(virtualLayout.getWidth(), width);
        this.height = height; //Math.min(virtualLayout.getHeight(), height);
        emptyLine = new AttributedStringBuilder().append(" ".repeat(this.width));
        if (offsetY + height > virtualLayout.getHeight()) { // We have a blank space at bottom
            // TODO : Limit scroll layout to be little as virtual layout
            offsetY = virtualLayout.getHeight() - height;
            if (offsetY < 0) offsetY = 0;
        }

    }

    @Override
    public AttributedStringBuilder render(int y) {
        if (y + offsetY >= virtualLayout.getHeight()) {
            return emptyLine;
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