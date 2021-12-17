package org.peekmoon.kafkat;

import org.jline.keymap.KeyMap;
import org.jline.terminal.Terminal;
import org.peekmoon.kafkat.action.Action;
import org.peekmoon.kafkat.action.OpenClusterAction;
import org.peekmoon.kafkat.configuration.ClusterConfiguration;
import org.peekmoon.kafkat.tui.InnerLayout;
import org.peekmoon.kafkat.tui.StackSizeMode;
import org.peekmoon.kafkat.tui.Table;
import org.peekmoon.kafkat.tui.HorizontalAlign;

import java.util.List;

public class ClustersPage extends Page {

    private static final String COL_NAME_CLUSTER_NAME = "name";
    private static final String COL_NAME_BOOTSTRAP = "bootstrap servers";
    private final Table table;
    private final List<ClusterConfiguration> clusters;

    public ClustersPage(Application application, List<ClusterConfiguration> clusters) {
        super(application);
        this.clusters = clusters;
        this.table = new Table("clusters");
        table.addColumn(COL_NAME_CLUSTER_NAME, HorizontalAlign.LEFT, StackSizeMode.SIZED, 10);
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
        // FIXME : Remove old values
        for (int i = 0; i < clusters.size(); i++) {
            var cluster = clusters.get(i);
            table.putRow(Integer.toString(i), cluster.name, cluster.bootstrapServers.get(0));
        }
    }

    @Override
    KeyMap<Action> getKeyMap(Terminal terminal) {
        var keyMap = new KeyMap<Action>();
        TableKeyMapProvider.fill(table, keyMap, terminal);
        keyMap.bind(new OpenClusterAction(application, this::getCurrentCluster), "\r");
        return keyMap;
    }

    public ClusterConfiguration getCurrentCluster() {
        int clusterIdx = Integer.parseInt(table.getCurrentSelection());
        return clusters.get(clusterIdx);
    }

}
