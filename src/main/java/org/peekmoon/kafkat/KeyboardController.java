package org.peekmoon.kafkat;

import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.terminal.Terminal;
import org.jline.utils.NonBlockingReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;

public class KeyboardController implements Runnable {

    private final static Logger log = LoggerFactory.getLogger(KeyboardController.class);

    private final BindingReader bindingReader;
    private final KeyMap<Action> keyMap;
    private final Queue<Action> actions;
    private KeyMap<Action> localKeyMap;

    public KeyboardController(Application application, Terminal terminal, Queue<Action> actions) {
        log.info("Initializing console reader");
        this.actions = actions;
        bindingReader = new BindingReader(terminal.reader());
        this.keyMap = application.getKeyMap();
    }

    public synchronized void setLocalKeyMap(KeyMap<Action> localKeyMap) {
        log.info("Switching keyMap");
        // Remove previous localKeyMap from KayMap
        if (this.localKeyMap != null) {
            this.localKeyMap.getBoundKeys().forEach((k,v) -> keyMap.unbind(k));
        }
        // Add new localKeyMap to globalKeyMap
        this.localKeyMap = localKeyMap;
        localKeyMap.getBoundKeys().forEach((k,v) -> keyMap.bind(v,k));
    }

    @Override
    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
            if (bindingReader.peekCharacter(100) != NonBlockingReader.READ_EXPIRED) {
                synchronized(this) {
                    var action = bindingReader.readBinding(keyMap);
                    actions.add(action);
                }
            }
        }
    }
}
