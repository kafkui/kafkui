package org.peekmoon.kafkat;

import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.Uuid;

public class TopcisView extends TableView { // TODO : Should extends or compose ?

    public TopcisView(Display display, int w, int h) {
        super(display, w, h);
        addColumn("name", 40);
        addColumn("policy", 10);
    }

    public void update(String name, Config config) { // FIXME: Bug can be called during rendering :(
        addRow(name, config.get("cleanup.policy").value());
    }

}
