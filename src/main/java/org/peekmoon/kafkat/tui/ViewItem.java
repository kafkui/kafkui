package org.peekmoon.kafkat.tui;

import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

class ViewItem {

    private final ViewLayout parent;
    private final String key;
    private int width;
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
        line = new AttributedStringBuilder().append(value);
        setValue(line);

    }

    void setValue(AttributedStringBuilder line) {
        this.line = line;
        this.width = Math.max(line.columnLength(), width);
        adjustLineLength();
    }

    void setWidth(int width) {
        this.width = width;
        adjustLineLength();
    }

    int getWidth() {
        return width;
    }

    AttributedStringBuilder render() {
        return line;
    }

    private void adjustLineLength() {
        var length = line.columnLength();
        if (length < width) {
            String padding = " ".repeat(width - length);
            switch (parent.getAlign()) {
                case LEFT -> line.append(padding);
                case RIGHT -> line = new AttributedStringBuilder().append(padding).append(line);
            }
        }
    }

}
