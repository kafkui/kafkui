package org.peekmoon.kafkat.action;

import org.peekmoon.kafkat.Application;
import org.peekmoon.kafkat.RecordPage;
import org.peekmoon.kafkat.TopicsPage;

public class SwitchToRecordsAction implements Action {

    private final Application application;
    private final TopicsPage topcisPage;

    public SwitchToRecordsAction(Application application, TopicsPage topcisPage) {
        this.application = application;
        this.topcisPage = topcisPage;
    }

    @Override
    public void apply() {
        var recordPage = new RecordPage(application, topcisPage);
        application.switchPage(recordPage);
    }
}
