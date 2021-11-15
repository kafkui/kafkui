package org.peekmoon.kafkat;

import org.peekmoon.kafkat.tui.*;

public class ConsumersPage implements Page {

    private static final String COL_NAME_GROUP_ID = "GROUP ID";

    private final Table table;

    public ConsumersPage() {
        this.table = new Table();
        table.addColumn(COL_NAME_GROUP_ID, StackSizeMode.PROPORTIONAL, 1);
    }

    public InnerLayout getTable() {
        return table;
    }

    public void add(String groupId) {
        table.putRow(groupId, groupId);
    }

    @Override
    public void process(Application.Operation op) {
        switch (op) {
            case UP -> table.selectUp();
            case DOWN -> table.selectDown();
        }
    }


}
