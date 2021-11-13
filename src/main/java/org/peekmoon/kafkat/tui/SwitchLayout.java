package org.peekmoon.kafkat.tui;

import org.jline.utils.AttributedStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class SwitchLayout extends InnerLayout {

    private final static Logger log = LoggerFactory.getLogger(SwitchLayout.class);

    private final Map<String, InnerLayout> children = new HashMap<>();
    private InnerLayout currentLayout = new EmptyLayout();

    public void add(String name, InnerLayout child) {
        child.setParent(this);
        children.put(name, child);
    }

    public void switchTo(String name) {
        var child = children.get(name);
        if (child == null) {
            throw new IllegalArgumentException("Unknown layout " + name);
        }
        currentLayout = child;
        invalidate();
    }

    @Override
    public int getWidth() {
        return currentLayout.getWidth();
    }

    @Override
    public int getHeight() {
        return currentLayout.getHeight();
    }

    @Override
    public void resize(int width, int height) {
        log.debug("Resizing : {} to {},{}", this, width, height);
        children.values().forEach(c -> c.resize(width, height));
        log.debug("Resized : {}", this);
    }

    @Override
    public AttributedStringBuilder render(int y) {
        return currentLayout.render(y);
    }


}
