package org.peekmoon.kafkat.action;

import org.peekmoon.kafkat.Application;
import org.peekmoon.kafkat.ClustersPage;
import org.peekmoon.kafkat.TopicsPage;

public class OpenClusterAction implements Action {

    private final Application application;
    private final ClustersPage clustersPage;

    public OpenClusterAction(Application application, ClustersPage clustersPage) {
        this.application = application;
        this.clustersPage = clustersPage;
    }

    @Override
    public void apply() {
        // FIXME : Memory leak
        var admin = application.openKafkaAdmin(clustersPage.getCurrentCluster());
        var topicsPage = new TopicsPage(application, admin, clustersPage);
        application.switchPage(topicsPage);
    }
}
