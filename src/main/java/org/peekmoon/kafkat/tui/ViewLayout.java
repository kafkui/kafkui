package org.peekmoon.kafkat.tui;

import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * A vertical list of items. Every item is a line on terminal.
 * ViewLayoutSize is always the size of it's content. All lines have the same width which is the width of the longest line
 */
public class ViewLayout extends InnerLayout {

    private final static Logger log = LoggerFactory.getLogger(ViewLayout.class);

    private final Map<String, ViewItem> items;
    private final HorizontalAlign align;

    private int width;
    private final List<String> order;


    public ViewLayout(String name) {
        this(name, HorizontalAlign.LEFT);
    }

    public ViewLayout(String name, HorizontalAlign align) {
        super(name);
        this.items = new HashMap<>();
        this.order = new ArrayList<>();
        this.width = 0;
        this.align = align;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return items.size();
    }

    @Override
    public void resize(int width, int height) {
        log.debug("Resizing : {} to {},{}", this, width, height);
        log.debug("Resized  : {}", this);
    }

    @Override
    public synchronized AttributedStringBuilder render(int y) {
        String key = order.get(y);
        ViewItem viewItem = items.get(key);
        return viewItem.render();
    }

    public void putItem(String key, String value) {
        putItem(key, value, AttributedStyle.DEFAULT);
    }

    // FIXME : Synchronize with rezising and drawing
    // TODO : Reduce width if removing the longest value
    public synchronized void putItem(String key, String value, AttributedStyle style) {
        var item = items.get(key);
        if (item == null) {
            item = new ViewItem(this, key, value, style);
            items.put(key, item);
            order.add(key);
        } else {
            item.setValue(value);
        }
        var newItemWidth = item.getWidth();
        if (newItemWidth > width) {
            items.values().forEach(i -> i.setWidth(newItemWidth));
            width = newItemWidth;
        } else {
            item.setWidth(width);
        }
    }

    public synchronized void removeItem(String key) {
        if (!order.remove(key) || items.remove(key) == null) {
            throw new IllegalArgumentException("Unable to remove key " + key);
        }

    }

    public void setOrder(List<String> keyOrder) {
        if (keyOrder.size() != items.size()) {
            throw new IllegalArgumentException("Only supported total ordering " + keyOrder.size() +"<>" + items.size());
        }
    }

    protected HorizontalAlign getAlign() {
        return align;
    }


}
