package org.peekmoon.kafkat.tui;

import org.jline.utils.AttributedStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class StackHorizontalLayout extends InnerLayout {

    private final static Logger log = LoggerFactory.getLogger(StackHorizontalLayout.class);

    private List<StackItem> items = new ArrayList<>();

    StackHorizontalLayout(String name) {
        super(name);
    }

    @Override
    public int getWidth() {
        return items.stream()
                .mapToInt(StackItem::getWidth)
                .sum();
    }

    @Override
    public int getHeight() {
        return items.stream()
                .mapToInt(StackItem::getHeight)
                .max().orElse(0);
    }

    @Override
    public void resize(int width, int height) {
        log.debug("Resizing : {} to {},{}", this, width, height);

        int totalSize = -0;
        int totalProportion = 0;
        int nbProportional = 0;

        for (StackItem item : items) {
            switch ( item.getMode()) {
                case SIZED -> totalSize += item.getValue();
                case CONTENT -> totalSize += item.getContentWidth() + 1;
                case PROPORTIONAL -> {
                    totalProportion += item.getValue();
                    nbProportional ++;
                }
            }
        }

        int spaceToShare = width - totalSize;

        for (StackItem item : items) {
            switch (item.getMode()) {
                case SIZED -> item.resize(item.getValue(), height);
                case CONTENT -> item.resize(item.getContentWidth() + 1, height);
                case PROPORTIONAL -> item.resize(spaceToShare * totalProportion / totalProportion, height);
            }
        }

        log.debug("Resized  : {}", this);
    }

    @Override
    public AttributedStringBuilder render(int y) {
        AttributedStringBuilder builder = new AttributedStringBuilder();
        items.forEach(l -> builder.append(buildLine(l.getScrollLayout(), y)));
        return builder;
    }

    private AttributedStringBuilder buildLine(InnerLayout inner, int y) {
        return y < inner.getHeight() ? inner.render(y) : emptyLine(inner.getWidth());
    }


    public void add(InnerLayout innerLayout, StackSizeMode mode, int value) {
        items.add(new StackItem(this, innerLayout, mode, value));
    }

    public void adjustHeightToContent() {
        items.forEach(StackItem::adjustHeightToContent);
    }

    public void adjustWidthTo(StackHorizontalLayout other) {
        for (int i=0; i< items.size(); i++) {
            items.get(i).setMinWidth(other.items.get(i).getWidth());
            items.get(i).setMaxWidth(other.items.get(i).getWidth());
        }

    }


}
