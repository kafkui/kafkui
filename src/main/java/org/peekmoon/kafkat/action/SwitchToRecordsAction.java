package org.peekmoon.kafkat.action;

import org.peekmoon.kafkat.Application;
import org.peekmoon.kafkat.RecordPage;
import org.peekmoon.kafkat.TopicsPage;

public class SwitchToRecordsAction implements Action {

    private final Application application;
    private final TopicsPage topicsPage;

    public SwitchToRecordsAction(Application application, TopicsPage topicsPage) {
        this.application = application;
        this.topicsPage = topicsPage;
    }

    @Override
    public void apply() {
        var recordPage = new RecordPage(application, topicsPage);
        application.switchPage(recordPage);
    }
}
