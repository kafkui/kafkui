package org.peekmoon.kafkat;

import org.peekmoon.kafkat.tui.Table;

public class TableSelectDownAction implements Action {

    private final Table table;

    public TableSelectDownAction(Table table) {
        this.table = table;
    }

    @Override
    public void apply() {
        table.selectDown();
    }
}
