package org.peekmoon.kafkat;

import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyboardController {

    private final static Logger log = LoggerFactory.getLogger(KeyboardController.class);

    private final BindingReader bindingReader;
    private final KeyMap<Application.Operation> keyMap;
    private KeyMap<Application.Operation> localKeyMap;

    public KeyboardController(Terminal terminal) {
        log.info("Initializing console reader");
        bindingReader = new BindingReader(terminal.reader());

        keyMap = new KeyMap<>();
        keyMap.setAmbiguousTimeout(200);
        keyMap.setNomatch(Application.Operation.NONE);
        keyMap.bind(Application.Operation.SEARCH, "/");
        keyMap.bind(Application.Operation.EXIT, "q", KeyMap.esc() );
        keyMap.bind(Application.Operation.SWITCH_TO_CONSUMER, ":c");
        keyMap.bind(Application.Operation.SWITCH_TO_TOPICS, ":t");
        keyMap.bind(Application.Operation.SWITCH_TO_RECORDS, ":r");
    }

    public void setLocalKeyMap(KeyMap<Application.Operation> localKeyMap) {
        log.info("Switching keyMap");
        // Remove previous localKeyMap from KayMap
        if (this.localKeyMap != null) {
            this.localKeyMap.getBoundKeys().forEach((k,v) -> keyMap.unbind(k));
        }
        // Add new localKeyMap to globalKeyMap
        this.localKeyMap = localKeyMap;
        localKeyMap.getBoundKeys().forEach((k,v) -> keyMap.bind(v,k));
    }


    public Application.Operation getEvent() {
        Application.Operation op = bindingReader.readBinding(keyMap);
        log.info("Receiving a new operation {}", op);
        return op;
    }

}
