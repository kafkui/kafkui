package org.peekmoon.kafkui.configuration;

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

    // FIXME: If file does not exists exception
    public static Configuration read() {
        try {
            var configFile = getConfigFile();
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            return mapper.readValue(configFile.toFile(), Configuration.class);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read config file", e);
        }
    }

    public static Path getConfigFile() {
        var userHomeDir = System.getProperty("user.home");
        return Path.of(userHomeDir, ".kafkui.conf.yaml");
    }

    public List<ClusterConfiguration> clusters = new ArrayList<>();

    public static boolean fileExists() {
        return getConfigFile().toFile().canRead();
    }

    public static boolean canRead() {
        try {
            read();
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }
}
