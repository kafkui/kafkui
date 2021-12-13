package org.peekmoon.kafkat;

import org.jline.keymap.KeyMap;
import org.jline.terminal.Terminal;
import org.peekmoon.kafkat.tui.InnerLayout;

public interface Page {
    String getId();
    InnerLayout getLayout();
    void activate();
    void deactivate();
    KeyMap<Action> getKeyMap(Terminal terminal);
}
