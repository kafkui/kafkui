package org.peekmoon.kafkat;

import org.peekmoon.kafkat.tui.InnerLayout;
import org.peekmoon.kafkat.tui.Page;
import org.peekmoon.kafkat.tui.Table;

public class ConsumersPage implements Page {

    private final Table table;

    public ConsumersPage() {
        this.table = new Table();
        table.addColumn("groupId");
    }

    public InnerLayout getTable() {
        return table;
    }

    public void add(String name, String policy) {
        table.addRow(name, policy);
    }

    public void add(String groupId) {
        table.addRow(groupId);
    }

    @Override
    public void activate() {

    }

    @Override
    public void process(Application.Operation op) {
        switch (op) {
            case UP -> table.selectUp();
            case DOWN -> table.selectDown();
        }
    }


}
