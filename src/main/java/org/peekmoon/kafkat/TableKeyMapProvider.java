package org.peekmoon.kafkat;

import org.jline.keymap.KeyMap;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;
import org.peekmoon.kafkat.action.Action;
import org.peekmoon.kafkat.action.TableSelectDownAction;
import org.peekmoon.kafkat.action.TableSelectUpAction;
import org.peekmoon.kafkat.tui.Table;

public class TableKeyMapProvider {

    public static void fill(Table table, KeyMap<Action> keyMap, Terminal terminal) {
        keyMap.bind(new TableSelectUpAction(table), KeyMap.key(terminal, InfoCmp.Capability.key_up));
        keyMap.bind(new TableSelectDownAction(table), KeyMap.key(terminal, InfoCmp.Capability.key_down));
    }
}
