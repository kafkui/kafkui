package org.peekmoon.kafkat.action;

import org.peekmoon.kafkat.Application;
import org.peekmoon.kafkat.TopicsPage;
import org.peekmoon.kafkat.configuration.ClusterConfiguration;

import java.util.function.Supplier;

public class OpenClusterAction implements Action {

    private final Application application;
    private final Supplier<ClusterConfiguration> clusterSupplier;

    public OpenClusterAction(Application application, Supplier<ClusterConfiguration> clusterSupplier) {
        this.application = application;
        this.clusterSupplier = clusterSupplier;
    }

    @Override
    public void apply() {
        // FIXME : Memory leak
        var admin = application.openKafkaAdmin(clusterSupplier.get());
        var topicsPage = new TopicsPage(application, admin);
        application.switchPage(topicsPage);
    }
}
