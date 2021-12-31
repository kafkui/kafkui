package org.peekmoon.kafkui.action;

import org.peekmoon.kafkui.Application;

public class ExitAction implements Action {

    private final Application application;

    public ExitAction(Application application) {
        this.application = application;
    }

    @Override
    public void apply() {
        application.exit();
    }
}
