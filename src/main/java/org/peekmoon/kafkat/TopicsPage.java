package org.peekmoon.kafkat;

import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.TopicDescription;
import org.peekmoon.kafkat.tui.Page;
import org.peekmoon.kafkat.tui.StackSizeMode;
import org.peekmoon.kafkat.tui.Table;

public class TopicsPage implements Page {

    private final Table table;

    public TopicsPage() {
        this.table = new Table();
        //table.addColumn("uuid");
        table.addColumn("name", StackSizeMode.PROPORTIONAL, 1);
        table.addColumn("part", StackSizeMode.SIZED, 5);
        table.addColumn("repl", StackSizeMode.SIZED, 5);
        table.addColumn("policy", StackSizeMode.SIZED, 7);
    }

    public void add(String name, String policy) {
        table.putRow(name, name, policy);
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

    public void addTopic(String name) {
        table.putValue(name, "name", name);
    }

    public Void setConfig(String name, Config conf) {
        table.putValue(name, "policy", conf.get("cleanup.policy").value());
        return null;
    }

    public Void setDescription(TopicDescription description) {
        //table.putValue(description.name(), "uuid", description.topicId().toString());
        int partitions = description.partitions().size();
        table.putValue(description.name(), "part", String.valueOf(partitions));
        int replicas = description.partitions().get(0).replicas().size();
        table.putValue(description.name(), "repl", String.valueOf(replicas));


        return null;
    }
}
