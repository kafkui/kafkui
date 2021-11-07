package org.peekmoon.kafkat.tui;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;

public class ConstrainedSizeLayout extends InnerLayout {

    private int minWidth, maxWidth;
    private int minHeight, maxHeight;
    private int width, height;

    private AttributedStringBuilder emptyLine;


    private final InnerLayout inner;

    public ConstrainedSizeLayout(InnerLayout inner) {
        this.inner = inner;
        inner.setParent(this);
        this.minWidth = this.minHeight = 0;
        this.maxWidth = this.maxHeight = Integer.MAX_VALUE;
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
        this.width = width;
        this.height = height;

        enforceConstraints();

        inner.resize(this.width, this.height);
    }

    @Override
    public AttributedStringBuilder render(int y) {

        if (y>=maxHeight) {
            return emptyLine;
        }

        var innerLine = inner.render(y);

        if (innerLine.columnLength() > width) {
            AttributedString subSequence = innerLine.subSequence(0, width);
            return new AttributedStringBuilder().append(subSequence);
        }

        if (innerLine.columnLength() <= width) {
            return innerLine.append(" ".repeat(width - innerLine.columnLength()));
        }

        throw new IllegalStateException("Unable to get case");
    }

    private void enforceConstraints() {
        // apply limits
        width = Math.max(width, minWidth);
        width = Math.min(width, maxWidth);

        height = Math.max(height, minHeight);
        height = Math.min(height, maxHeight);

        emptyLine = new AttributedStringBuilder().append(" ".repeat(width));
    }

}
