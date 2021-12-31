package org.peekmoon.kafkui.action;

import org.peekmoon.kafkui.Application;
import org.peekmoon.kafkui.Page;

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
