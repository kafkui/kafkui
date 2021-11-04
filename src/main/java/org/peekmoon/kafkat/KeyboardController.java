package org.peekmoon.kafkat;

import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;

public class KeyboardController implements Runnable {

    private final static Logger log = LoggerFactory.getLogger(KeyboardController.class);


    private final BindingReader bindingReader;
    private final KeyMap<Application.Operation> keyMap;
    private final Queue<Application.Operation> queue;

    public KeyboardController(Terminal terminal, Queue<Application.Operation> queue) {
        log.info("Initializing console reader");
        bindingReader = new BindingReader(terminal.reader());

        keyMap = new KeyMap<>();
        keyMap.setAmbiguousTimeout(200);
        keyMap.setNomatch(Application.Operation.NONE);
        keyMap.bind(Application.Operation.SEARCH, "/");
        keyMap.bind(Application.Operation.EXIT, ":", KeyMap.esc());
        keyMap.bind(Application.Operation.UP, KeyMap.key(terminal, InfoCmp.Capability.key_up));
        keyMap.bind(Application.Operation.DOWN, KeyMap.key(terminal, InfoCmp.Capability.key_down));
        this.queue = queue;
    }


    @Override
    public void run() {

        while (true) {
            Application.Operation op = bindingReader.readBinding(keyMap);
            log.info("Receiving a new operation {}", op);
            if (op != Application.Operation.NONE) queue.add(op);

        }

    }
}
