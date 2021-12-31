package org.peekmoon.kafkui.action;

import org.peekmoon.kafkui.tui.Table;

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
