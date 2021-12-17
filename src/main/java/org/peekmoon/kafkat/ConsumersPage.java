package org.peekmoon.kafkat;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.KafkaFuture;
import org.jline.keymap.KeyMap;
import org.jline.terminal.Terminal;
import org.peekmoon.kafkat.action.Action;
import org.peekmoon.kafkat.tui.*;
import org.peekmoon.kafkat.tui.HorizontalAlign;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class ConsumersPage extends Page {

    private static final String COL_NAME_GROUP_ID = "id";
    private static final String COL_NAME_GROUP_STATE = "State";

    private final Admin kafkaAdmin;
    private final Table table;

    public ConsumersPage(Application application, Admin kafkaAdmin) {
        super(application);
        this.kafkaAdmin = kafkaAdmin;
        this.table = new Table("consumers");
        table.addColumn(COL_NAME_GROUP_ID, HorizontalAlign.LEFT, StackSizeMode.PROPORTIONAL, 1);
        table.addColumn(COL_NAME_GROUP_STATE, HorizontalAlign.LEFT, StackSizeMode.SIZED, 10);
    }

    public InnerLayout getLayout() {
        return table;
    }

    @Override
    public String getId() {
        return "PAGE_CONSUMERS";
    }

    @Override
    public void update() {
        try {
            kafkaAdmin.listConsumerGroups().all()
                    .thenApply(c -> askConsumerDescription(kafkaAdmin, c))
                    .get()
                    .thenApply(this::updateConsumer)
                    .get();

        } catch (ExecutionException | InterruptedException e) {
            throw new IllegalStateException("Unable to retrive topics data", e);
        }
    }

    @Override
    public KeyMap<Action> getKeyMap(Terminal terminal) {
        var keyMap = new KeyMap<Action>();
        TableKeyMapProvider.fill(table, keyMap, terminal);
        return keyMap;
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
