package org.peekmoon.kafkat;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.ConsumerGroupDescription;
import org.apache.kafka.clients.admin.ConsumerGroupListing;
import org.apache.kafka.common.KafkaFuture;
import org.peekmoon.kafkat.tui.*;
import org.peekmoon.kafkat.tui.VerticalAlign;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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

    @Override
    public void process(Application.Operation op) {
        switch (op) {
            case UP -> table.selectUp();
            case DOWN -> table.selectDown();
        }
    }

    public void update(Admin admin) {
        try {

            admin.listConsumerGroups().all()
                    .thenApply(c -> askConsumerDescription(admin, c))
                    .get()
                    .thenApply(c -> updateConsumer(c))
                    .get();

        } catch (ExecutionException | InterruptedException e) {
            throw new IllegalStateException("Unable to retrive topics data", e);
        }
    }

    private KafkaFuture<Map<String, ConsumerGroupDescription>> askConsumerDescription(Admin admin, Collection<ConsumerGroupListing> consumers) {
        var groupeIds = consumers.stream()
                .map(ConsumerGroupListing::groupId)
                .collect(Collectors.toList());
        return admin.describeConsumerGroups(groupeIds).all();
    }

    private Void updateConsumer(Map<String, ConsumerGroupDescription> descriptions) {
        for (ConsumerGroupDescription description : descriptions.values()) {
            table.putRow(description.groupId(), description.groupId(), description.state().toString());
        }
        return null;
    }


}
