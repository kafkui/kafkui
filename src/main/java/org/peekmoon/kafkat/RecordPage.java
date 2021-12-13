package org.peekmoon.kafkat;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.InterruptException;
import org.jline.keymap.KeyMap;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;
import org.peekmoon.kafkat.tui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class RecordPage implements Page {

    private static final String COL_NAME_PARTITION = "partition";
    private static final String COL_NAME_OFFSET = "offset";
    private static final String COL_NAME_VALUE = "value";

    private final static Logger log = LoggerFactory.getLogger(RecordPage.class);


    private final String topic;
    private final Table table;
    private final KafkaConsumer<String, String> consumer;


    public RecordPage(String topic) {

        this.topic = topic;
        this.table = new Table("records");
        table.addColumn(COL_NAME_PARTITION, VerticalAlign.LEFT, StackSizeMode.SIZED, 10);
        table.addColumn(COL_NAME_OFFSET, VerticalAlign.LEFT, StackSizeMode.SIZED, 10);
        table.addColumn(COL_NAME_VALUE, VerticalAlign.LEFT, StackSizeMode.PROPORTIONAL, 1);

        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "breisen.datamix.ovh:9093");
        props.setProperty("group.id", "test");
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topic));
    }

    @Override
    public String getId() {
        return "PAGE_RECORDS_" + topic;
    }


    private Thread t;

    @Override
    public void activate() {
        t = new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    log.info("Request new items from topic");
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(5000));
                    log.info("Received {} values", records.count());
                    for (ConsumerRecord<String, String> record : records) {
                        table.putRow(getUid(record), String.valueOf(record.partition()), String.valueOf(record.offset()), record.value());
                    }
                } catch (InterruptException e) {
                    log.info("Stopping pooling from kafa");
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    @Override
    public void deactivate() {
        try {
            t.interrupt();
            t.join();
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        } finally {
            close();
        }
    }

    @Override
    public KeyMap<Action> getKeyMap(Terminal terminal) {
        var keyMap = new KeyMap<Action>();
        TableKeyMapProvider.fill(table, keyMap, terminal);
        return keyMap;
    }

    public InnerLayout getLayout() {
        return table;
    }


    private String getUid(ConsumerRecord<String, String> record) {
        return String.format("%d/%d", record.partition(), record.offset());
    }

    public void close() {
        consumer.close();
    }
}
