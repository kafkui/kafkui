package org.peekmoon.kafkat;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.config.ConfigResource;

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

        /*
        var topicDescription = admin.describeTopics(topicsList).all().get(); // TODO : Replace par un value and async reply to terminal
        topicDescription.values().stream()
                .forEach(td -> writer.write(td.topicId().toString() + '\t' + td.name() + '\n'));
        //.flatMap(td -> td.partitions().stream())
        //.forEach(pd -> writer.write("\t\t" + pd.partition() + '\n'));

        writer.write("Node\n");
        admin.describeCluster().nodes().get().forEach(n -> writer.write(n.id() + "--" + n.port() + "\n"));

         */

    }

    public void update(ConsumersPage view) {
        client.listConsumerGroups().all().thenApply(c -> update(view, c));
    }

    private Void update(ConsumersPage view, Collection<ConsumerGroupListing> consumers) {
        var groupeIds = consumers.stream().map(c -> c.groupId()).collect(Collectors.toList());
        client.describeConsumerGroups(groupeIds)
                .describedGroups().values()
                .forEach((descriptionFuture -> descriptionFuture.thenApply(description -> update(view, description))));
        return null;
    }

    private Void update(ConsumersPage view, ConsumerGroupDescription description) {
        view.add(description.groupId());
        return null;
    }


    private Void update(TopicsPage view, Collection<TopicListing> topics) {

        var topicsConfig = topics.stream()
                .map(t -> new ConfigResource(ConfigResource.Type.TOPIC, t.name()))
                .collect(Collectors.toSet());
        client.describeConfigs(topicsConfig).values()
                .forEach((configResource, f) -> f.thenApply(conf -> update(view, configResource.name(), conf)));
        return null;
    }

    private Void update(TopicsPage model, String name, Config description) {
        model.add(name, description.get("cleanup.policy").value());

        return null;
    }
}
