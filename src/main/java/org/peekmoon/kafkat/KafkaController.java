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
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "breisen.datamix.ovh:9093");
        client = AdminClient.create(config);
    }


    public void update(TopcisView model) {
        client.listTopics().listings().thenApply(t -> update(model, t));

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

    private Void update(TopcisView model, Collection<TopicListing> topics) {

        var topicsConfig = topics.stream()
                .map(t -> new ConfigResource(ConfigResource.Type.TOPIC, t.name()))
                .collect(Collectors.toSet());
        client.describeConfigs(topicsConfig).values()
                .forEach((configResource, f) -> f.thenApply(conf -> update(model, configResource.name(), conf)));
        return null;
    }

    private Void update(TopcisView model, String name, Config description) {
        // model.update(name, description);
        return null;
    }
}
