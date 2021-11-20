package org.peekmoon.kafkat;

import org.apache.kafka.clients.admin.ConsumerGroupDescription;
import org.peekmoon.kafkat.tui.*;
import org.peekmoon.kafkat.tui.VerticalAlign;

public class ConsumersPage implements Page {

    private static final String COL_NAME_GROUP_ID = "id";
    private static final String COL_NAME_GROUP_STATE = "State";


    private final Table table;

    public ConsumersPage() {
        this.table = new Table();
        table.addColumn(COL_NAME_GROUP_ID, VerticalAlign.LEFT, StackSizeMode.PROPORTIONAL, 1);
        table.addColumn(COL_NAME_GROUP_STATE, VerticalAlign.LEFT, StackSizeMode.SIZED, 10);
    }

    public InnerLayout getTable() {
        return table;
    }

    public void setDescription(ConsumerGroupDescription group) {
        table.putRow(group.groupId(), group.groupId(), group.state().toString());
    }

    @Override
    public void process(Application.Operation op) {
        switch (op) {
            case UP -> table.selectUp();
            case DOWN -> table.selectDown();
        }
    }


}
