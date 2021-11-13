package org.peekmoon.kafkat.tui;

import org.jline.utils.AttributedStringBuilder;
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


    public Table() {
        this.masterLayout = new StackVerticalLayout();

        this.titlesLayout = new StackHorizontalLayout();

        this.contentLayout = new StackHorizontalLayout();
        this.selectableLayout = new SelectableLayout(contentLayout);
        this.scrollLayout = new ScrollLayout(selectableLayout);

        this.masterLayout.add(titlesLayout);
        this.masterLayout.add(scrollLayout);
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

    public void addColumn(String name) {
        var col = new Column();
        contentLayout.add(col.getLayout());
        columns.add(col);
        columnMap.put(name, col);
        titlesLayout.add(col.getTitleLayout());
    }

    public void putRow(String key, String... cols) {
        if (cols.length != columns.size()) {
            throw new IllegalArgumentException("Bad col number " + cols.length + " != " + columns.size());
        }

        for (int noCol = 0; noCol<columns.size(); noCol++) {
            columns.get(noCol).putItem(key, cols[noCol]);
        }
        invalidate();
    }

    // TODO : Check if key already knew unless add key in every cols
    public void putValue(String key, String colName, String value) {
        columnMap.get(colName).putItem(key, value);
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
        scrollLayout.makeVisible(selectableLayout.getSelectedOffet());
        log.debug("Resized : {}", this);
    }

    @Override
    public AttributedStringBuilder render(int y) {
        return masterLayout.render(y);
    }


    private class Column {
        private final ViewLayout titleLayout;
        private final ViewLayout contentLayout;
        private final ScrollLayout scroller;
        private final ConstrainedSizeLayout sizer;


        private Column() {
            this.titleLayout = new ViewLayout();
            this.titleLayout.putItem("Title","$");
            this.contentLayout = new ViewLayout();
            this.scroller = new ScrollLayout(contentLayout);
            this.sizer = new ConstrainedSizeLayout(scroller);
        }

        public InnerLayout getLayout() {
            return sizer;
        }

        public void putItem(String key, String col) {
            contentLayout.putItem(key, col);
            sizer.setMinWidth(contentLayout.getWidth());
            sizer.setMaxHeight(contentLayout.getHeight());
            sizer.setMinHeight(contentLayout.getHeight());
        }

        public InnerLayout getTitleLayout() {
            return titleLayout;
        }
    }




}

