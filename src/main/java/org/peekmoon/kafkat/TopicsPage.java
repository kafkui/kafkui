package org.peekmoon.kafkat;

import org.peekmoon.kafkat.tui.Page;
import org.peekmoon.kafkat.tui.Table;

public class TopicsPage implements Page {

    private final Table table;

    public TopicsPage() {
        this.table = new Table();
        table.addColumn("name");
        table.addColumn("policy");
    }

    public void add(String name, String policy) {
        table.addRow(name, policy);
    }

    public Table getTable() {
        return table;
    }

    @Override
    public void process(Application.Operation op) {
        switch (op) {
            case UP -> table.selectUp();
            case DOWN -> table.selectDown();
        }
    }
}
