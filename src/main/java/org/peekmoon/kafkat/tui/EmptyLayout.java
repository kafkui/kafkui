package org.peekmoon.kafkat.tui;

import org.jline.utils.AttributedStringBuilder;

public class EmptyLayout extends InnerLayout {

    private AttributedStringBuilder line = new AttributedStringBuilder();
    private int width, height;

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
        this.width = width;
        this.height = height;
        this.line = new AttributedStringBuilder().append(" ".repeat(width));

    }

    @Override
    public AttributedStringBuilder render(int y) {
        if (y<0 || y>=height) {
            throw new IllegalArgumentException("Line out of bounds " + y + " : " + height);
        }
        return line;
    }

}
