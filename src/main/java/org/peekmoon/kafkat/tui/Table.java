package org.peekmoon.kafkat.tui;

import org.jline.utils.AttributedStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Table extends InnerLayout {

    private final static Logger log = LoggerFactory.getLogger(Table.class);


    private final ScrollLayout scrollLayout;
    private final SelectableLayout selectableLayout;
    private final StackLayout layout;
    private final List<Column> columns = new ArrayList<>();


    public Table() {
        this.layout = new StackLayout();
        this.selectableLayout = new SelectableLayout(layout);
        this.scrollLayout = new ScrollLayout(selectableLayout);
        this.scrollLayout.setParent(this);
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
        layout.add(col.getLayout());
        columns.add(col);
    }

    public void addRow(String... cols) {
        if (cols.length != columns.size()) {
            throw new IllegalArgumentException("Bad col number " + cols.length + " != " + columns.size());
        }

        for (int noCol = 0; noCol<columns.size(); noCol++) {
            columns.get(noCol).addItem(cols[noCol]);
        }
        invalidate();
    }

    @Override
    public int getWidth() {
        return scrollLayout.getWidth();
    }

    @Override
    public int getHeight() {
        return scrollLayout.getHeight();
    }

    @Override
    public void resize(int width, int height) {
        scrollLayout.resize(width, height);
        scrollLayout.makeVisible(selectableLayout.getSelectedOffet());
        log.debug("Resized : {}", this);
    }

    @Override
    public AttributedStringBuilder render(int y) {
        return scrollLayout.render(y);
    }


    private class Column {
        private final ViewLayout view;
        private final ScrollLayout scroller;
        private final ConstrainedSizeLayout sizer;


        private Column() {
            this.view = new ViewLayout();
            this.scroller = new ScrollLayout(view);
            this.sizer = new ConstrainedSizeLayout(scroller);
        }

        public InnerLayout getLayout() {
            return sizer;
        }

        public void addItem(String col) {
            view.addItem(col);
            sizer.setMinWidth(view.getWidth());
            sizer.setMaxHeight(view.getHeight());
            sizer.setMinHeight(view.getHeight());
        }
    }




}

