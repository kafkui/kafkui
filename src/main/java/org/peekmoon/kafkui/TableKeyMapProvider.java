package org.peekmoon.kafkui;

import org.jline.keymap.KeyMap;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;
import org.peekmoon.kafkui.action.Action;
import org.peekmoon.kafkui.action.TableSelectDownAction;
import org.peekmoon.kafkui.action.TableSelectUpAction;
import org.peekmoon.kafkui.tui.Table;

public class TableKeyMapProvider {

    public static void fill(Table table, KeyMap<Action> keyMap, Terminal terminal) {
        keyMap.bind(new TableSelectUpAction(table), KeyMap.key(terminal, InfoCmp.Capability.key_up));
        keyMap.bind(new TableSelectDownAction(table), KeyMap.key(terminal, InfoCmp.Capability.key_down));
    }
}
