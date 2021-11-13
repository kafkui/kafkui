package org.peekmoon.kafkat.tui;

import org.jline.utils.AttributedStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewLayout extends InnerLayout {

    private final static Logger log = LoggerFactory.getLogger(ViewLayout.class);

    private final Map<String, ViewItem> items;

    private int width;
    private List<String> order;

    public ViewLayout() {
        this.items = new HashMap<>();
        this.order = new ArrayList<>();
        this.width = 0;
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
        log.warn("Ignoring view layout resized : {} to {},{}", this, width, height);
    }

    @Override
    public AttributedStringBuilder render(int y) {
        String key = order.get(y);
        return items.get(key).render();
    }

    // TODO : Synchronize with rezising and drawing
    // TODO : Reduce width if set a value shorter
    public void putItem(String key, String value) {
        var item = items.get(key);
        if (item == null) {
            item = new ViewItem(this, key, value);
            items.put(key, item);
            order.add(key);
        } else {
            item.setValue(value);
        }
        var itemWidth = item.getWidth();
        if (itemWidth > width) {
            items.values().forEach(i -> i.setWidth(width));
        }
    }

    public void setOrder(List<String> keyOrder) {
        if (keyOrder.size() != items.size()) {
            throw new IllegalArgumentException("Only supported total ordering " + keyOrder.size() + items.size());
        }

    }

}
