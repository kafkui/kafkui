package org.peekmoon.kafkui.action;

import org.peekmoon.kafkui.Application;
import org.peekmoon.kafkui.RecordPage;
import org.peekmoon.kafkui.TopicsPage;

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
