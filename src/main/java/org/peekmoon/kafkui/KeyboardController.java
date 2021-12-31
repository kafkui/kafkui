package org.peekmoon.kafkui;

import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.terminal.Terminal;
import org.jline.utils.NonBlockingReader;
import org.peekmoon.kafkui.action.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class KeyboardController implements Runnable {

    private final static Logger log = LoggerFactory.getLogger(KeyboardController.class);

    private final BindingReader bindingReader;
    private final BlockingQueue<Action> actions;
    private final Application application;
    private KeyMap<Action> currentKeyMap;

    public KeyboardController(Application application, Terminal terminal, BlockingQueue<Action> actions) {
        log.info("Initializing console reader");
        this.actions = actions;
        bindingReader = new BindingReader(terminal.reader());
        this.application = application;
        this.currentKeyMap = application.buildKeyMap();
    }

    public synchronized void setLocalKeyMap(KeyMap<Action> localKeyMap) {
        log.info("Switching keyMap");
        currentKeyMap = application.buildKeyMap();
        localKeyMap.getBoundKeys().forEach((k,v) -> currentKeyMap.bind(v,k));
    }

    @Override
    public void run() {
        try {
            //noinspection InfiniteLoopStatement
            while (true) {
                if (bindingReader.peekCharacter(1000) != NonBlockingReader.READ_EXPIRED) {
                    Action action;
                    synchronized (this) {
                        action = bindingReader.readBinding(currentKeyMap);
                    }
                    var success = actions.offer(action, 5, TimeUnit.SECONDS);
                    if (!success) {
                        throw new IllegalStateException("Dead lock detected : " + actions.size());
                    }

                }
            }
        } catch (InterruptedException e) {
            throw new IllegalStateException("We should not receive interupt on this thread", e);
        }
    }
}
