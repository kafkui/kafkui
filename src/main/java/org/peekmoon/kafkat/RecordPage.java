package org.peekmoon.kafkat;

import org.jline.keymap.KeyMap;
import org.jline.terminal.Terminal;
import org.peekmoon.kafkat.action.Action;
import org.peekmoon.kafkat.action.SwitchToPageAction;
import org.peekmoon.kafkat.tui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public class RecordPage extends Page {

    private static final String COL_NAME_PARTITION = "partition";
    private static final String COL_NAME_OFFSET = "offset";
    private static final String COL_NAME_VALUE = "value";

    private final static Logger log = LoggerFactory.getLogger(RecordPage.class);

    private final String topic;
    private final TopicsPage topicsPage;
    private final Table table;
    private final ConsumerThread consumerThread;
    private final AtomicBoolean askingThreadStop;


    public RecordPage(Application application, TopicsPage topicsPage) {
        super(application);
        this.topicsPage = topicsPage;
        this.topic = topicsPage.getCurrentTopic();
        this.table = new Table("records");
        table.addColumn(COL_NAME_PARTITION, HorizontalAlign.LEFT, StackSizeMode.SIZED, 10);
        table.addColumn(COL_NAME_OFFSET, HorizontalAlign.LEFT, StackSizeMode.SIZED, 10);
        table.addColumn(COL_NAME_VALUE, HorizontalAlign.LEFT, StackSizeMode.PROPORTIONAL, 1);

        this.askingThreadStop = new AtomicBoolean(false);
        consumerThread = new ConsumerThread(topic, table, askingThreadStop);

    }

    @Override
    public String getId() {
        return "PAGE_RECORDS_" + topic;
    }


    @Override
    public void activate() {
        var t = new Thread(consumerThread, "Consumer-" + topic);
        t.setDaemon(true);
        t.start();
    }

    @Override
    public void deactivate() {
        log.info("Deactivating");
        askingThreadStop.set(true);
    }

    @Override
    public KeyMap<Action> getKeyMap(Terminal terminal) {
        var keyMap = new KeyMap<Action>();
        TableKeyMapProvider.fill(table, keyMap, terminal);
        keyMap.bind(new SwitchToPageAction(application, topicsPage), KeyMap.esc(), KeyMap.del() );
        return keyMap;
    }

    public InnerLayout getLayout() {
        return table;
    }


}
