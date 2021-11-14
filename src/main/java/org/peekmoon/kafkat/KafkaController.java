package org.peekmoon.kafkat;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.config.ConfigResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class KafkaController {

    private final static Logger log = LoggerFactory.getLogger(KafkaController.class);

    private final AdminClient client;

    public KafkaController() {
        Properties config = new Properties();
        //config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9193");
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "breisen.datamix.ovh:9093");
        client = AdminClient.create(config);
    }


    ///////////////////
    // Topics part


    public void update(TopicsPage view)  {

        try {

            var topicListing = client.listTopics().listings().get();

            var configResourceList = new ArrayList<ConfigResource>();
            var topicsName = new ArrayList<String>();
            for (TopicListing topic : topicListing) {
                String topicName = topic.name();
                view.addTopic(topicName);
                configResourceList.add(new ConfigResource(ConfigResource.Type.TOPIC, topicName));
                topicsName.add(topicName);
            }

            KafkaFuture.allOf(
                    client.describeTopics(topicsName).all().thenApply(r -> updateTopicsDescription(view, r)),
                    client.describeConfigs(configResourceList).all().thenApply(r -> updateTopicsConfig(view, r))
            ).get();

        } catch (ExecutionException | InterruptedException e) {
            throw new IllegalStateException("Unable to retrive topics data", e);
        }

    }

    private Void updateTopicsConfig(TopicsPage view, Map<ConfigResource, Config> configs) {
        for (Map.Entry<ConfigResource, Config> entry : configs.entrySet()) {
            view.setConfig(entry.getKey().name(), entry.getValue());
        }
        return null;
    }

    private Void updateTopicsDescription(TopicsPage view, Map<String, TopicDescription> descriptions) {
        for (TopicDescription description : descriptions.values()) {
            view.setDescription(description);
        }
        return null;
    }


    ///////////////////
    // Consumers part

    public void update(ConsumersPage view) {
        try {

            client.listConsumerGroups().all()
                    .thenApply(c -> askConsumerDescription(view, c))
                    .get()
                    .thenApply(c -> updateConsumer(view, c))
                    .get();

        } catch (ExecutionException | InterruptedException e) {
            throw new IllegalStateException("Unable to retrive topics data", e);
        }
    }

    private KafkaFuture<Map<String, ConsumerGroupDescription>> askConsumerDescription(ConsumersPage view, Collection<ConsumerGroupListing> consumers) {
        var groupeIds = consumers.stream().map(ConsumerGroupListing::groupId).collect(Collectors.toList());
        return client.describeConsumerGroups(groupeIds).all();
    }

    private Void updateConsumer(ConsumersPage view, Map<String, ConsumerGroupDescription> descriptions) {
        for (ConsumerGroupDescription description : descriptions.values()) {
            view.add(description.groupId());
        }
        return null;
    }

}
