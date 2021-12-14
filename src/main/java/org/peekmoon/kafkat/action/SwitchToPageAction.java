package org.peekmoon.kafkat.action;

import org.peekmoon.kafkat.Application;
import org.peekmoon.kafkat.Page;

public class SwitchToPageAction implements Action {

    private final Application application;
    private final Page page;

    public SwitchToPageAction(Application application, Page page) {
        this.application = application;
        this.page = page;
    }

    @Override
    public void apply() {
        application.switchPage(page);
    }
}
