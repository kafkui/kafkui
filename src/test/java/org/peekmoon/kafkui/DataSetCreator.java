package org.peekmoon.kafkui;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DataSetCreator {

    private static AdminClient kafkaAdmin;

    @BeforeAll
    public static void prepareConnection() {
        Properties config = new Properties();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9193,localhost:9293");
        kafkaAdmin = AdminClient.create(config);
    }

    @Test
    public void buildSimpleDataSet() throws ExecutionException, InterruptedException {

        var topics = Stream.of(
                new NewTopic("simple", 1, (short) 1),
                new NewTopic("cities", 10, (short) 3),
                new NewTopic("blabla.envet", 3, (short) 3),
                new NewTopic("supercharger", 1, (short) 2),
                new NewTopic("castratorine", 10, (short) 2),
                new NewTopic("glouton", 25, (short) 5)
        );
        //admin.deleteTopics(testTopics().stream().map(t->t.name()).collect(Collectors.toList())).all().get();
        buildTopics(topics);
    }

    @Test
    public void buildLongDataSet() throws ExecutionException, InterruptedException {
        buildTopics(IntStream.range(0,200)
                .mapToObj(i -> new NewTopic("test_topic_" + i, 1, (short)2)));
    }

    private void buildTopics(Stream<NewTopic> topics) throws InterruptedException, ExecutionException {
        Set<String> existingTopics = kafkaAdmin.listTopics().names().get();
        var topicsToBuild = topics.filter(n->!existingTopics.contains(n.name())).collect(Collectors.toList());
        var topicResult = kafkaAdmin.createTopics(topicsToBuild);
        topicResult.all().get();
    }




}
