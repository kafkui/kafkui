package org.peekmoon.kafkat;

import org.jline.utils.AttributedString;

import java.util.List;

public class TableView extends View {

    private final Views<ListView> colViews;

    public TableView(Display display, int w, int h) {
        super(display, w, h);
        this.colViews = new Views<>(display, w, h);
    }

    public void selectUp() {
        colViews.forEach(ListView::selectUp);
    }

    public void selectDown() {
        colViews.forEach(ListView::selectDown);
    }

    @Override
    public List<AttributedString> render() {
        return colViews.render();
    }

    protected void addColumn(String name, int width) {
        ListView colview = new ListView(display, width, getHeight());
        colViews.add(colview, getTotalColWidth(), 0);
    }

    protected void addRow(String... cols) {
        if (cols.length != colViews.size()) {
            throw new IllegalArgumentException("Bad col number " + cols.length + " != " + colViews.size());
        }

        for (int noCol = 0; noCol< colViews.size(); noCol++) {
            colViews.get(noCol).add(cols[noCol]);
        }
    }
    
    private int getTotalColWidth() {
        return colViews.stream().mapToInt(v -> v.getWidth()).sum();
    }
}
