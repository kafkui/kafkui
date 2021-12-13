package org.peekmoon.kafkat;

import java.util.function.Supplier;

public class SwitchToRecordsAction implements Action {

    private final Application application;
    private final Supplier<String> topicSupplier;

    public SwitchToRecordsAction(Application application, Supplier<String> topicSupplier) {
        this.application = application;
        this.topicSupplier = topicSupplier;
    }

    @Override
    public void apply() {
        var recordPage = new RecordPage(topicSupplier.get());
        application.switchPage(recordPage);
    }
}
