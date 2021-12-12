package org.peekmoon.kafkat.tui;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScrollLayout extends InnerLayout {

    private final static Logger log = LoggerFactory.getLogger(ScrollLayout.class);

    private final VerticalAlign align;

    private int minWidth, maxWidth;
    private int minHeight, maxHeight;
    private int width, height;
    private int offsetX, offsetY;

    private final InnerLayout inner;

    public ScrollLayout(String name, InnerLayout inner) {
        this(name, inner, VerticalAlign.LEFT);
    }

    public ScrollLayout(String name, InnerLayout inner, VerticalAlign align) {
        super(name);
        this.inner = inner;
        inner.setParent(this);
        this.minWidth = this.minHeight = 0;
        this.maxWidth = this.maxHeight = Integer.MAX_VALUE;
        this.align = align;
        this.offsetX = this.offsetY = 0;
    }

    public void setMinWidth(int minWidth) {
        this.minWidth = minWidth;
        enforceConstraints();
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
        enforceConstraints();
    }

    public void setMinHeight(int minHeight) {
        this.minHeight = minHeight;
        enforceConstraints();
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
        enforceConstraints();
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
        log.debug("Resizing : {} to {},{}", this, width, height);
        this.width = width;
        this.height = height;

        enforceConstraints();

        inner.resize(this.width, this.height);

        if (offsetY + this.height > inner.getHeight()) { // Stay inside when becoming bigger
            offsetY = Math.max(0, inner.getHeight() - height);
        }

        log.debug("Resized  : {}", this);
    }

    @Override
    public AttributedStringBuilder render(int y) {

        if (y + offsetY >= inner.getHeight()) {
            return emptyLine();
        }

        var innerLine = inner.render(y + offsetY);

        if (innerLine.columnLength() > width) {
            AttributedString subSequence = innerLine.subSequence(0, width);
            return new AttributedStringBuilder().append(subSequence);
        }

        if (innerLine.columnLength() <= width) {
            String padding = " ".repeat(width - innerLine.columnLength());
            return switch (align) {
                case LEFT -> innerLine.append(padding);
                case RIGHT -> new AttributedStringBuilder().append(padding).append(innerLine);
            };
        }

        throw new IllegalStateException("Unable to get case");
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

    private void enforceConstraints() {
        // apply limits
        width = Math.max(width, minWidth);
        width = Math.min(width, maxWidth);

        height = Math.max(height, minHeight);
        height = Math.min(height, maxHeight);
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
    public String toString() {
        return super.toString() + " over [" + inner.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + "] {" +
                minWidth + "<w<" + maxWidth + " " +
                minHeight + "<h<" + maxHeight +
                '}';
    }
}

