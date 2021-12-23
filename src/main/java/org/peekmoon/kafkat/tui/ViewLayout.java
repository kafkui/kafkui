package org.peekmoon.kafkat.tui;

import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/***
 * A vertical list of items. Every item is a line on terminal.
 * ViewLayoutSize is always the size of it's content. All lines have the same width which is the width of the longest line
 */
public class ViewLayout extends InnerLayout {

    private final static Logger log = LoggerFactory.getLogger(ViewLayout.class);

    private final Map<String, ViewItem> items;
    private final HorizontalAlign align;

    private int width;
    private ViewItem longestItem;
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

    /***
     * A View layout have always the width of it longest element
     * and the height equal to the number of elements in it
     */
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

    /***
     * Add an item to this viewLayout, synchronizing same width, based on longest item, on all inner viewItem
     */
    public synchronized void putItem(String key, String value, AttributedStyle style) {
        // Add item to structure
        var item = items.get(key);
        if (item == null) {
            item = new ViewItem(this, key, value, style);
            items.put(key, item);
            order.add(key);
        } else {
            item.setValue(value);
        }


        // Update new layout width
        var newItemContentWidth = item.getContentWidth();
        if (newItemContentWidth > width) {
            longestItem = item;
            items.values().forEach(i -> i.setWidth(newItemContentWidth)); // Update width of all
            width = newItemContentWidth;
        } else  if (key.equals(longestItem.getKey())) { // We are reducing the longst item
            // Find the new longest one
            longestItem = items.values().stream().max(Comparator.comparingInt(ViewItem::getContentWidth)).get();
            int newMaxContentWidth = longestItem.getContentWidth();
            items.values().forEach(i -> i.setWidth(newMaxContentWidth)); // Update width of all
            width = newMaxContentWidth;
        } else {
            item.setWidth(width);
        }

    }

    // FIXME: Resize when remove longest item
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
