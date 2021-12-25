package org.peekmoon.kafkat;

import org.jline.keymap.KeyMap;
import org.jline.terminal.Terminal;
import org.peekmoon.kafkat.action.Action;
import org.peekmoon.kafkat.tui.InnerLayout;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Page {

    protected final Application application;
    private Thread thread;
    private AtomicBoolean askStop = new AtomicBoolean(false);

    protected Page(Application application) {
        this.application = application;
    }

    protected Runnable getUpdateRunnable(AtomicBoolean askStop) {
        return () -> {
            try {
                while (!askStop.get()) {
                    this.update();
                    Thread.sleep(1500);
                }
            } catch (InterruptedException e) {
                throw new IllegalStateException("Should not be interupted " + Page.this, e);
            } catch (KException e) {
                application.status("Error : " + e.getMessage());
            }
        };
    }

    abstract String getId();
    abstract InnerLayout getLayout();
    protected void update() throws KException {}

    abstract KeyMap<Action> getKeyMap(Terminal terminal);

    void activate() {
        if (thread != null) {
            throw new IllegalStateException("Try to activate an already activated page " + this);
        }
        thread = new Thread(getUpdateRunnable(askStop), getId());
        thread.start();
    }

    void deactivate() {
            askStop.set(true);
            thread = null;
    }
}
