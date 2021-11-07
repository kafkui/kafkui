package org.peekmoon.kafkat.tui;

import org.jline.utils.AttributedStringBuilder;

import java.util.ArrayList;
import java.util.List;

public class ViewLayout extends InnerLayout {

    private final List<AttributedStringBuilder> sequences;
    private int width;

    public ViewLayout() {
        this.sequences = new ArrayList<>();
        this.width = 0;
    }


    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return sequences.size();
    }


    @Override
    public void resize(int width, int height) {
        // ignoring event
    }

    @Override
    public AttributedStringBuilder render(int y) {
        return new AttributedStringBuilder().append(sequences.get(y));
    }


    public void addItem(String item) {
        var line = new AttributedStringBuilder().append(item);

        if (line.columnLength() >= width) {
            width = line.columnLength();
            sequences.forEach(l -> l.append(" ".repeat(width - l.columnLength())));
        } else {
            line.append(" ".repeat(width - line.columnLength()));
        }
        sequences.add(line);

    }

    public List<AttributedStringBuilder> getContent() {
        return sequences;
    }
}
