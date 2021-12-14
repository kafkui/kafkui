package org.peekmoon.kafkat.configuration;

import java.util.ArrayList;
import java.util.List;

public class ClusterConfiguration {
    public String name;
    public List<String> bootstrapServers = new ArrayList<>();
}
