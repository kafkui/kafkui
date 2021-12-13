package org.peekmoon.kafkat;

import org.jline.keymap.KeyMap;
import org.jline.terminal.Terminal;
import org.peekmoon.kafkat.tui.InnerLayout;

public abstract class Page {

    protected final Application application;

    protected Page(Application application) {
        this.application = application;
    }

    abstract String getId();
    abstract InnerLayout getLayout();
    abstract void activate();
    abstract void deactivate();
    abstract KeyMap<Action> getKeyMap(Terminal terminal);
}
