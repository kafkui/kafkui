package org.peekmoon.kafkui.action;

import org.peekmoon.kafkui.tui.Table;

public class TableSelectUpAction implements Action {

    private final Table table;

    public TableSelectUpAction(Table table) {
        this.table = table;
    }

    @Override
    public void apply() {
        table.selectUp();
    }
}
