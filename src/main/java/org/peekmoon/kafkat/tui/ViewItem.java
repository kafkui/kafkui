package org.peekmoon.kafkat.tui;

import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

class ViewItem {

    private final ViewLayout parent;
    private final String key;
    private int width;
    private int contentWidth;
    private AttributedStringBuilder line;

    ViewItem(ViewLayout parent, String key, String value) {
        this(parent, key, new AttributedStringBuilder().append(value));
    }

    ViewItem(ViewLayout parent, String key, String value, AttributedStyle style) {
        this(parent, key, new AttributedStringBuilder().append(value, style));
    }

    ViewItem(ViewLayout parent, String key, AttributedStringBuilder value) {
        this.parent = parent;
        this.key = key;
        setValue(value);
    }

    void setValue(String value) {
        setValue(new AttributedStringBuilder().append(value));
    }

    void setValue(AttributedStringBuilder line) {
        this.line = line;
        contentWidth = line.columnLength();
        width = Math.max(line.columnLength(), width);
        adjustLineLength();
    }

    void setWidth(int width) {
        this.width = width;
        adjustLineLength();
    }

    int getWidth() {
        return width;
    }

    int getContentWidth() {
        return contentWidth;
    }

    AttributedStringBuilder render() {
        return line;
    }

    private void adjustLineLength() {
        if (contentWidth < width) {
            String padding = " ".repeat(width - contentWidth);
            switch (parent.getAlign()) {
                case LEFT -> line.append(padding);
                case RIGHT -> line = new AttributedStringBuilder().append(padding).append(line);
            }
        }
    }

    public String getKey() {
        return key;
    }
}
