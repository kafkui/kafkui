package org.peekmoon.kafkat;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.config.ConfigResource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.stream.Collectors;

public class KafkaController {

    private final AdminClient client;

    public KafkaController() {
        Properties config = new Properties();
        //config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9193");
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "breisen.datamix.ovh:9093");
        client = AdminClient.create(config);
    }


    public void update(TopicsPage view) {
        client.listTopics().listings().thenApply(t -> update(view, t));
    }

    ///////////////////
    // Topics part

    private Void update(TopicsPage view, Collection<TopicListing> topics) {
        var configResourceList = new ArrayList<ConfigResource>();
        var topicsName = new ArrayList<String>();
        for (TopicListing topic : topics) {
            String topicName = topic.name();
            view.addTopic(topicName);
            configResourceList.add(new ConfigResource(ConfigResource.Type.TOPIC, topicName));
            topicsName.add(topicName);
        }

        client.describeConfigs(configResourceList).values()
                .forEach((configResource, f) -> f.thenApply(conf -> view.setConfig(configResource.name(), conf)));

        client.describeTopics(topicsName).values()
                .forEach((topicName, f) -> f.thenApply(view::setDescription));

        return null;
    }


    ///////////////////
    // Consumers part

    public void update(ConsumersPage view) {
        client.listConsumerGroups().all().thenApply(c -> update(view, c));
    }

    private Void update(ConsumersPage view, Collection<ConsumerGroupListing> consumers) {
        var groupeIds = consumers.stream().map(ConsumerGroupListing::groupId).collect(Collectors.toList());
        client.describeConsumerGroups(groupeIds)
                .describedGroups().values()
                .forEach((descriptionFuture -> descriptionFuture.thenApply(description -> update(view, description))));
        return null;
    }

    private Void update(ConsumersPage view, ConsumerGroupDescription description) {
        view.add(description.groupId());
        return null;
    }

}
