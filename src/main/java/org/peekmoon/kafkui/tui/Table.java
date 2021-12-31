package org.peekmoon.kafkui.tui;

import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table extends InnerLayout {

    private final static Logger log = LoggerFactory.getLogger(Table.class);

    private final StackVerticalLayout masterLayout;
    private final ScrollLayout scrollLayout;
    private final SelectableLayout selectableLayout;
    private final StackHorizontalLayout stackTitlesLayout;
    private final StackHorizontalLayout stackContentLayout;
    private final List<Column> columns = new ArrayList<>();
    private final Map<String, Column> columnMap = new HashMap<>();
    private final List<String> keys = new ArrayList<>();


    public Table(String name) {
        super("table-" + name);

        // Two stack the titles Line + All the content
        this.masterLayout = new StackVerticalLayout("master-Vertical-" + name);

        this.stackTitlesLayout = new StackHorizontalLayout("title-" + name);    // Stack all the columns title
        this.stackContentLayout = new StackHorizontalLayout("content-" + name); // Stack all the columns content

        this.selectableLayout = new SelectableLayout(stackContentLayout);
        this.scrollLayout = new ScrollLayout(  "scroll-" + getName(), selectableLayout); // All the content (main scroll)

        this.masterLayout.add(stackTitlesLayout, StackSizeMode.SIZED, 1);
        this.masterLayout.add(scrollLayout, StackSizeMode.PROPORTIONAL, 1);
        this.masterLayout.setParent(this);
    }


    public void selectUp() {
        int newOffset = selectableLayout.selectUp();
        scrollLayout.makeVisible(newOffset);
    }

    public void selectDown() {
        int newOffset = selectableLayout.selectDown();
        scrollLayout.makeVisible(newOffset);
    }

    public String getCurrentSelection() {
        var selectedOffet = selectableLayout.getSelectedOffet();
        return keys.get(selectedOffet);
    }

    public void addColumn(String title, HorizontalAlign align, StackSizeMode mode, int value) {
        var col = new Column(title, align, mode, value);
        columns.add(col);
        columnMap.put(title, col);
    }

    public synchronized void putRow(String key, String... cols) {
        if (cols.length != columns.size()) {
            throw new IllegalArgumentException("Bad col number " + cols.length + " != " + columns.size());
        }

        if (!keys.contains(key)) {
            keys.add(key);
        }

        for (int noCol = 0; noCol<columns.size(); noCol++) {
            columns.get(noCol).putItem(key, cols[noCol]);
        }
    }

    public synchronized void putValue(String key, String colName, String value) {
        Column column = columnMap.get(colName);
        if (!keys.contains(key)) {
           // This is a new line add value for eveny columns
           keys.add(key);
           columnMap.forEach((k,c) -> c.putItem(key, "?"));
        }
        column.putItem(key, value);
    }

    public synchronized void removeRow(String key) {
        if (!keys.contains(key)) {
            throw new IllegalArgumentException("Unable to find key " + key);
        }
        keys.remove(key);
        columnMap.values().forEach(c -> c.removeItem(key));
    }

    public synchronized int length() {
        return keys.size();
    }

    @Override
    public int getWidth() {
        return masterLayout.getWidth();
    }

    @Override
    public int getHeight() {
        return masterLayout.getHeight();
    }

    @Override
    public void resize(int width, int height) {
        log.debug("Resizing : {} to {},{}", this, width, height);
        masterLayout.resize(width, height);
        stackTitlesLayout.adjustWidthTo(stackContentLayout);
        scrollLayout.makeVisible(selectableLayout.getSelectedOffet());
        log.debug("Resized  : {}", this);
    }

    @Override
    public AttributedStringBuilder render(int y) {
        return masterLayout.render(y);
    }


    private class Column {
        private final ViewLayout titleLayout;
        private final ViewLayout contentLayout;

        private Column(String title, HorizontalAlign align,  StackSizeMode mode, int value) {
            this.titleLayout = new ViewLayout("col-" + title + "-content", align);
            this.contentLayout = new ViewLayout("col-" + title + "-view", align);
            stackTitlesLayout.add(titleLayout, mode, value);
            stackContentLayout.add(contentLayout, mode, value);
            titleLayout.putItem("title",title, AttributedStyle.BOLD);
        }

        public void putItem(String key, String col) {
            contentLayout.putItem(key, col);
        }

        public void removeItem(String key) {
            contentLayout.removeItem(key);
        }

    }




}

