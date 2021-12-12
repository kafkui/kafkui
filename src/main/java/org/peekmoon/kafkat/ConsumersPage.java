package org.peekmoon.kafkat;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.KafkaFuture;
import org.jline.keymap.KeyMap;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;
import org.peekmoon.kafkat.tui.*;
import org.peekmoon.kafkat.tui.VerticalAlign;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class ConsumersPage implements Page {

    private static final String COL_NAME_GROUP_ID = "id";
    private static final String COL_NAME_GROUP_STATE = "State";

    private final AdminClient client;
    private final Table table;

    public ConsumersPage() {
        this.table = new Table("consumers");
        table.addColumn(COL_NAME_GROUP_ID, VerticalAlign.LEFT, StackSizeMode.PROPORTIONAL, 1);
        table.addColumn(COL_NAME_GROUP_STATE, VerticalAlign.LEFT, StackSizeMode.SIZED, 10);

        // TODO : Only one client for all pages
        Properties config = new Properties();
        //config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9193");
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "breisen.datamix.ovh:9093");
        client = AdminClient.create(config);
    }

    public InnerLayout getLayout() {
        return table;
    }

    @Override
    public String getId() {
        return "PAGE_CONSUMERS";
    }

    @Override
    public void activate() {
        update(client);
    }

    @Override
    public void deactivate() {

    }

    @Override
    public KeyMap<Application.Operation> getKeyMap(Terminal terminal) {
        var keyMap = new KeyMap<Application.Operation>();
        keyMap.bind(Application.Operation.UP, KeyMap.key(terminal, InfoCmp.Capability.key_up));
        keyMap.bind(Application.Operation.DOWN, KeyMap.key(terminal, InfoCmp.Capability.key_down));
        return keyMap;
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
                    .thenApply(this::updateConsumer)
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
