package org.peekmoon.kafkui;

import org.jline.keymap.KeyMap;
import org.jline.terminal.Terminal;
import org.peekmoon.kafkui.action.Action;
import org.peekmoon.kafkui.action.OpenClusterAction;
import org.peekmoon.kafkui.configuration.ClusterConfiguration;
import org.peekmoon.kafkui.configuration.Configuration;
import org.peekmoon.kafkui.tui.HorizontalAlign;
import org.peekmoon.kafkui.tui.InnerLayout;
import org.peekmoon.kafkui.tui.StackSizeMode;
import org.peekmoon.kafkui.tui.Table;

import java.util.List;

public class ClustersPage extends Page {

    private static final String COL_NAME_CLUSTER_NAME = "name";
    private static final String COL_NAME_BOOTSTRAP = "bootstrap servers";
    private final Table table;
    private List<ClusterConfiguration> clusters;

    public ClustersPage(Application application) {
        super(application);
        this.table = new Table("clusters");
        table.addColumn(COL_NAME_CLUSTER_NAME, HorizontalAlign.LEFT, StackSizeMode.CONTENT, 10);
        table.addColumn(COL_NAME_BOOTSTRAP, HorizontalAlign.LEFT, StackSizeMode.PROPORTIONAL, 1);
    }


    @Override
    String getId() {
        return "PAGE_CLUSTERS";
    }

    @Override
    InnerLayout getLayout() {
        return table;
    }

    @Override
    protected void update() {
        clusters = Configuration.read().clusters;

        int  nbEntry = table.length();
        for (int i = 0; i < clusters.size(); i++) {
            var cluster = clusters.get(i);
            table.putRow(Integer.toString(i), cluster.name, cluster.bootstrapServers.get(0));
        }
        for (int i=clusters.size(); i<nbEntry; i++) {
            table.removeRow(Integer.toString(i));
        }
    }

    @Override
    KeyMap<Action> getKeyMap(Terminal terminal) {
        var keyMap = new KeyMap<Action>();
        TableKeyMapProvider.fill(table, keyMap, terminal);
        keyMap.bind(new OpenClusterAction(application, this), "\r");
        return keyMap;
    }

    public ClusterConfiguration getCurrentCluster() {
        int clusterIdx = Integer.parseInt(table.getCurrentSelection());
        return clusters.get(clusterIdx);
    }

}
