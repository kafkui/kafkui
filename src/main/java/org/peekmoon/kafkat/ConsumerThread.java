package org.peekmoon.kafkat;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.InterruptException;
import org.peekmoon.kafkat.tui.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConsumerThread implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(ConsumerThread.class);

    private final String topic;
    private final Table table;
    private final AtomicBoolean askStop;

    public ConsumerThread(String topic, Table table, AtomicBoolean askStop) {
        this.topic = topic;
        this.table = table;
        this.askStop = askStop;
    }

    @Override
    public void run() {

        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "breisen.datamix.ovh:9093");
        props.setProperty("group.id", "test");
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        try (var consumer = new KafkaConsumer<String, String>(props)) {

            consumer.subscribe(Collections.singletonList(topic));
            while (!askStop.get()) {
                log.info("Request new items from topic");
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                log.info("Received {} values", records.count());
                for (ConsumerRecord<String, String> record : records) {
                    table.putRow(getUid(record), String.valueOf(record.partition()), String.valueOf(record.offset()), record.value());
                }
            }

        } catch (InterruptException e) {
            log.info("Stopping pooling from kafka on interruption", e);
        }
        log.info("Consummer stopped");

    }

    private String getUid(ConsumerRecord<String, String> record) {
        return String.format("%d/%d", record.partition(), record.offset());
    }
}
