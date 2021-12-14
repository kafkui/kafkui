package org.peekmoon.kafkat.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Configuration {

//    static {
//        try {
//            Configuration configuration = new Configuration();
//            Cluster cluster = new Cluster();
//            cluster.name = "my-name";
//            cluster.bootstrapServers.add("bresien.to");
//            configuration.clusters.add(cluster);
//            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
//            mapper.writeValue(new File("test.yaml"), configuration);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public static Configuration read() {
        try {
            var userHomeDir = System.getProperty("user.home");
            var configFile = Path.of(userHomeDir, ".kafkat.conf.yaml");

            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            return mapper.readValue(configFile.toFile(), Configuration.class);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read config file", e);
        }
    }

    public List<ClusterConfiguration> clusters = new ArrayList<>();

}
