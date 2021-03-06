package org.peekmoon.kafkui.tui;

import org.jline.utils.AttributedStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class StackVerticalLayout extends InnerLayout {

    private final static Logger log = LoggerFactory.getLogger(StackVerticalLayout.class);


    private List<StackItem> inners = new ArrayList<>();

    public StackVerticalLayout(String name) {
        super(name);
    }


    @Override
    public int getWidth() {
        return inners.stream()
                .map(StackItem::getScrollLayout)
                .mapToInt(Layout::getWidth)
                .max().orElse(0);
    }

    @Override
    public int getHeight() {
        return inners.stream()
                .map(StackItem::getScrollLayout)
                .mapToInt(Layout::getHeight)
                .sum();
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

        int spaceToShare = height - totalSize;

        for (StackItem inner : inners) {
            switch (inner.getMode()) {
                case SIZED -> inner.resize(width, inner.getValue());
                case PROPORTIONAL -> inner.resize(width, spaceToShare * totalProportion / totalProportion);
            }
        }

        log.debug("Resized  : {}", this);
    }

    @Override
    public AttributedStringBuilder render(int y) {
        int offsetY = 0;
        for (StackItem item : inners) {
            if (y < offsetY + item.getScrollLayout().getHeight()) {
                return item.getScrollLayout().render(y - offsetY);
            }
            offsetY += item.getScrollLayout().getHeight();
        }
        throw new IllegalArgumentException("Unable to render line " + y + " max ofs : " + offsetY + " in " + this);
    }

    public void add(InnerLayout innerLayout, StackSizeMode mode, int value) {
        inners.add(new StackItem(this, innerLayout, mode, value));
    }


}
