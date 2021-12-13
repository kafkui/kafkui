package org.peekmoon.kafkat;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.config.ConfigResource;
import org.jline.keymap.KeyMap;
import org.jline.terminal.Terminal;
import org.peekmoon.kafkat.tui.StackSizeMode;
import org.peekmoon.kafkat.tui.Table;
import org.peekmoon.kafkat.tui.VerticalAlign;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class TopicsPage extends Page {

    public static final String COL_NAME_RETENTION_TIME = "max time";
    public static final String COL_NAME_RETENTION_SIZE = "max size";
    public static final String COL_NAME_TOPIC_NAME = "name";
    public static final String COL_NAME_NB_PARTITION = "part";
    public static final String COL_NAME_NB_REPLICA = "repl";
    public static final String COL_NAME_CLEANUP_POLICY = "policy";

    private final Application application;
    private final AdminClient client;
    private final Table table;

    public TopicsPage(Application application) {
        super(application);
        this.application = application;
        this.table = new Table("topics");
        table.addColumn(COL_NAME_TOPIC_NAME, VerticalAlign.LEFT, StackSizeMode.PROPORTIONAL, 1);
        table.addColumn(COL_NAME_NB_PARTITION, VerticalAlign.LEFT, StackSizeMode.SIZED, 5);
        table.addColumn(COL_NAME_NB_REPLICA, VerticalAlign.LEFT, StackSizeMode.SIZED, 5);
        table.addColumn(COL_NAME_CLEANUP_POLICY, VerticalAlign.LEFT, StackSizeMode.SIZED, 8);
        table.addColumn(COL_NAME_RETENTION_TIME, VerticalAlign.RIGHT, StackSizeMode.SIZED, 15);
        table.addColumn(COL_NAME_RETENTION_SIZE, VerticalAlign.RIGHT, StackSizeMode.SIZED, 10);

        Properties config = new Properties();
        //config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9193");
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "breisen.datamix.ovh:9093");
        client = AdminClient.create(config);
    }

    @Override
    public String getId() {
        return "PAGE_TOPICS";
    }

    public void add(String name, String policy) {
        table.putRow(name, name, policy);
    }

    public Table getLayout() {
        return table;
    }

    @Override
    public void activate() {
        update(client);
    }

    @Override
    public void deactivate() {

    }

    @Override
    public KeyMap<Action> getKeyMap(Terminal terminal) {
        var keyMap = new KeyMap<Action>();
        TableKeyMapProvider.fill(table, keyMap, terminal);
        keyMap.bind(new SwitchToRecordsAction(application, table::getCurrentSelection), "\r");
        return keyMap;
    }

    public String getCurrentTopic() {
        return table.getCurrentSelection();
    }

    public void update(AdminClient client)  {

        try {
            var topicListing = client.listTopics().listings().get();

            var configResourceList = new ArrayList<ConfigResource>();
            var topicsName = new ArrayList<String>();
            for (TopicListing topic : topicListing) {
                String topicName = topic.name();
                table.putValue(topicName, "name", topicName);
                configResourceList.add(new ConfigResource(ConfigResource.Type.TOPIC, topicName));
                topicsName.add(topicName);
            }

            KafkaFuture.allOf(
                    client.describeTopics(topicsName).all().thenApply(this::updateTopicsDescription),
                    client.describeConfigs(configResourceList).all().thenApply(this::updateTopicsConfig)
            ).get();

        } catch (ExecutionException | InterruptedException e) {
            throw new IllegalStateException("Unable to retrieve topics data", e);
        }

    }

    private Void updateTopicsConfig(Map<ConfigResource, Config> configs) {
        for (Map.Entry<ConfigResource, Config> entry : configs.entrySet()) {
            String name = entry.getKey().name();
            Config conf = entry.getValue();
            final String cleanupPolicy = conf.get("cleanup.policy").value();
            table.putValue(name, "policy", cleanupPolicy);
            switch (cleanupPolicy) {
                case "delete" -> {
                    table.putValue(name, COL_NAME_RETENTION_TIME, getRetentionTime(conf));
                    table.putValue(name, COL_NAME_RETENTION_SIZE, getRetentionSize(conf));
                }
                case "compact" -> {
                    table.putValue(name, COL_NAME_RETENTION_TIME, "-");
                    table.putValue(name, COL_NAME_RETENTION_SIZE, "-");
                }
            }

        }
        return null;
    }

    private Void updateTopicsDescription(Map<String, TopicDescription> descriptions) {
        for (TopicDescription description : descriptions.values()) {
            int partitions = description.partitions().size();
            table.putValue(description.name(), "part", String.valueOf(partitions));
            int replicas = description.partitions().get(0).replicas().size();
            table.putValue(description.name(), "repl", String.valueOf(replicas));
        }
        return null;
    }

    private String getRetentionTime(Config config) {
        var param = config.get("retention.ms");
        var value = Long.parseLong(param.value());
        if (value == -1) return "ထ ";
        return humanReadableDuration(Duration.ofMillis(value));
    }

    private String getRetentionSize(Config config) {
        var param = config.get("retention.bytes");
        var value = Long.parseLong(param.value());
        if (value == -1) return "ထ ";
        return humanReadableByteCount(value, false);
    }

    private String humanReadableDuration(Duration duration) {
        StringBuilder result = new StringBuilder();

        long days = duration.toDays();
        if (days > 0) {
            result.append(days).append(" day");
            if (days > 1) {
                result.append("s");
            }
        }

        if (duration.toHoursPart()>0 || duration.toMinutesPart()>0) {
            result.append(String.format(" %02dH", duration.toHoursPart()));
            result.append(String.format("%02dmn", duration.toMinutesPart()));
            if (duration.toSecondsPart()>0) {
                result.append(String.format("%02ds", duration.toSecondsPart()));
            }
        }

        return result.toString();
    }

    // From: https://programming.guide/worlds-most-copied-so-snippet.html
    private String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        long absBytes = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absBytes < unit) return bytes + " B";
        int exp = (int) (Math.log(absBytes) / Math.log(unit));
        long th = (long) Math.ceil(Math.pow(unit, exp) * (unit - 0.05));
        if (exp < 6 && absBytes >= th - ((th & 0xFFF) == 0xD00 ? 51 : 0)) exp++;
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        if (exp > 4) {
            bytes /= unit;
            exp -= 1;
        }
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

}
