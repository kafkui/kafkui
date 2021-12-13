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
    private final Queue<Action> actions;
    private final Application application;
    private KeyMap<Action> currentKeyMap;

    public KeyboardController(Application application, Terminal terminal, Queue<Action> actions) {
        log.info("Initializing console reader");
        this.actions = actions;
        bindingReader = new BindingReader(terminal.reader());
        this.application = application;
    }

    public synchronized void setLocalKeyMap(KeyMap<Action> localKeyMap) {
        log.info("Switching keyMap");
        currentKeyMap = application.buildKeyMap();
        localKeyMap.getBoundKeys().forEach((k,v) -> currentKeyMap.bind(v,k));
    }

    @Override
    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
            if (bindingReader.peekCharacter(1000) != NonBlockingReader.READ_EXPIRED) {
                synchronized(this) {
                    var action = bindingReader.readBinding(currentKeyMap);
                    actions.add(action);
                }
            }
        }
    }
}
