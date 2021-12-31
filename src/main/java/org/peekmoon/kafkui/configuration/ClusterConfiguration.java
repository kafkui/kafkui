package org.peekmoon.kafkui.configuration;

import java.util.ArrayList;
import java.util.List;

public class ClusterConfiguration {
    public String name;
    public List<String> bootstrapServers = new ArrayList<>();

    public String bootstrapServersAsString() {
        return String.join(",", bootstrapServers);
    }
}
