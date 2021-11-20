package org.peekmoon.kafkat.tui;

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
    private final StackHorizontalLayout contentLayout;
    private final StackHorizontalLayout titlesLayout;
    private final List<Column> columns = new ArrayList<>();
    private final Map<String, Column> columnMap = new HashMap<>();
    private final List<String> keys = new ArrayList<>();


    public Table() {
        this.masterLayout = new StackVerticalLayout();

        this.titlesLayout = new StackHorizontalLayout();

        this.contentLayout = new StackHorizontalLayout();
        this.selectableLayout = new SelectableLayout(contentLayout);
        this.scrollLayout = new ScrollLayout(selectableLayout);

        this.masterLayout.add(titlesLayout, StackSizeMode.SIZED, 1);
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

    public void addColumn(String title, VerticalAlign align, StackSizeMode mode, int value) {
        var col = new Column(title, align);
        contentLayout.add(col.getLayout(), mode, value);
        columns.add(col);
        columnMap.put(title, col);
        titlesLayout.add(col.getTitleLayout(), mode, value);
    }

    public synchronized void putRow(String key, String... cols) {
        if (cols.length != columns.size()) {
            throw new IllegalArgumentException("Bad col number " + cols.length + " != " + columns.size());
        }

        if (!keys.contains(key)) keys.add(key);

        for (int noCol = 0; noCol<columns.size(); noCol++) {
            columns.get(noCol).putItem(key, cols[noCol]);
        }
        invalidate();
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
        scrollLayout.resize(width, height-1);
        scrollLayout.makeVisible(selectableLayout.getSelectedOffet());
        log.debug("Resized : {}", this);
    }

    @Override
    public AttributedStringBuilder render(int y) {
        return masterLayout.render(y);
    }


    private class Column {
        private final ScrollLayout titleLayout;
        private final ViewLayout contentLayout;
        private final ScrollLayout scroller;


        private Column(String title, VerticalAlign align) {
            var titleLayout = new ViewLayout(align);
            titleLayout.putItem("Title",title, AttributedStyle.BOLD);
            this.titleLayout = new ScrollLayout(titleLayout, align);
            this.contentLayout = new ViewLayout(align);
            this.scroller = new ScrollLayout(contentLayout, align);
        }

        public InnerLayout getLayout() {
            return scroller;
        }

        public void putItem(String key, String col) {
            contentLayout.putItem(key, col);
            scroller.setMinWidth(contentLayout.getWidth());
            scroller.setMaxHeight(contentLayout.getHeight());
            scroller.setMinHeight(contentLayout.getHeight());
        }

        public InnerLayout getTitleLayout() {
            return titleLayout;
        }
    }




}

