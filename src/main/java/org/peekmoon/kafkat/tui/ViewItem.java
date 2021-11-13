package org.peekmoon.kafkat.tui;

import org.jline.utils.AttributedStringBuilder;

public class ViewItem {

    private final ViewLayout parent;
    private final String key;
    private int width;
    private AttributedStringBuilder line;

    public ViewItem(ViewLayout parent, String key, String value) {
        this.parent = parent;
        this.key = key;
        setValue(value);
    }

    public void setValue(String value) {
        line = new AttributedStringBuilder().append(value);
        this.width = Math.max(line.columnLength(), width);
        adjustLineLength();
    }

    public void setWidth(int width) {
        this.width = width;
        adjustLineLength();
    }

    public int getWidth() {
        return width;
    }

    public AttributedStringBuilder render() {
        return line;
    }

    private void adjustLineLength() {
        var length = line.columnLength();
        if (length < width) {
            line.append(" ".repeat(width - length));
        }
    }

}
