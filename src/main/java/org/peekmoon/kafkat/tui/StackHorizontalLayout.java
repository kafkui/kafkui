package org.peekmoon.kafkat.tui;

import org.jline.utils.AttributedStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class StackHorizontalLayout extends InnerLayout {

    private final static Logger log = LoggerFactory.getLogger(StackHorizontalLayout.class);

    private List<StackItem> inners = new ArrayList<>();

    StackHorizontalLayout(String name) {
        super(name);
    }

    @Override
    public int getWidth() {
        return inners.stream()
                .map(StackItem::getInner)
                .mapToInt(Layout::getWidth)
                .sum();
    }

    @Override
    public int getHeight() {
        return inners.stream()
                .map(StackItem::getInner)
                .mapToInt(Layout::getHeight)
                .max().orElse(0);
    }

    @Override
    public void resize(int width, int height) {
        log.debug("Resizing : {} to {},{}", this, width, height);

        int totalSize = -0;
        int totalProportion = 0;
        int nbProportional = 0;

        for (StackItem inner : inners) {
            switch ( inner.getMode()) {
                case SIZED -> totalSize += inner.getValue();
                case PROPORTIONAL -> {
                    totalProportion += inner.getValue();
                    nbProportional ++;
                }
            }
        }

        int spaceToShare = width - totalSize;

        for (StackItem inner : inners) {
            switch (inner.getMode()) {
                case SIZED -> inner.resize(inner.getValue(), height);
                case PROPORTIONAL -> inner.resize(spaceToShare * totalProportion / totalProportion, height);
            }
        }

        log.debug("Resized  : {}", this);
    }

    @Override
    public AttributedStringBuilder render(int y) {
        AttributedStringBuilder builder = new AttributedStringBuilder();
        inners.forEach(l -> builder.append(buildLine(l.getInner(), y)));
        return builder;
    }

    private AttributedStringBuilder buildLine(InnerLayout inner, int y) {
        return y < inner.getHeight() ? inner.render(y) : emptyLine(inner.getWidth());
    }


    public void add(InnerLayout innerLayout, StackSizeMode mode, int value) {
        innerLayout.setParent(this);
        inners.add(new StackItem(innerLayout, mode, value));
    }


}
