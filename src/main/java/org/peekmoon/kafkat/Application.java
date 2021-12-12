package org.peekmoon.kafkat;

 import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.peekmoon.kafkat.tui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

 import static org.jline.terminal.TerminalBuilder.PROP_DISABLE_ALTERNATE_CHARSET;

public class Application  {

    static {
        LogInitializer.init();
    }

    private final static Logger log = LoggerFactory.getLogger(Application.class.getName());


    public static void main(String[] args) {

        new Application().run();
    }

    private KeyboardController keyboardController;
    private SwitchLayout switchLayout;
    private TopicsPage topicsPage;
    private ConsumersPage consumersPage;
    private RecordPage recordPage;
    private Page currentPage;

    public void run() {

        log.info("Starting app's");

        // Set a default handler to log all exceptions
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            org.slf4j.Logger log = LoggerFactory.getLogger(Application.class);
            log.error("Uncaugh error", e);
        });


        // Workaround terminal with TERM=screen-256color display bad frame border
        System.setProperty(PROP_DISABLE_ALTERNATE_CHARSET, "true");


        try (Terminal terminal = TerminalBuilder.builder().build();
             Display display = new Display(terminal, buildLayout())) {


            // TODO : Better implementation as the action queue. The display thread should be using the action queue ?
            keyboardController = new KeyboardController(terminal);

            Thread displayThread = new Thread(display);
            displayThread.setDaemon(true);
            displayThread.start();

            currentPage = topicsPage;
            switchLayout.switchTo(topicsPage.getId());
            keyboardController.setLocalKeyMap(topicsPage.getKeyMap(terminal));
            topicsPage.activate();
            boolean askQuit = false;
            while (!askQuit){

                Operation op = keyboardController.getEvent();
                log.info("Receiving an new event {}", op);
                switch (op) {
                    case EXIT -> askQuit = true;
                    case SWITCH_TO_CONSUMER -> switchPage(terminal, consumersPage);
                    case SWITCH_TO_TOPICS -> switchPage(terminal, topicsPage);
                    case SWITCH_TO_RECORDS -> {
                        this.recordPage = new RecordPage(topicsPage.getCurrentTopic());
                        switchPage(terminal, recordPage);
                    }
                }
                currentPage.process(op);
            }

            currentPage.deactivate();


        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void switchPage(Terminal terminal, Page page) {
        switchLayout.add(page.getId(), page.getLayout());
        currentPage.deactivate();
        page.activate();
        keyboardController.setLocalKeyMap(page.getKeyMap(terminal));
        switchLayout.switchTo(page.getId());
        currentPage = page;
    }

    private InnerLayout buildLayout() {
        this.topicsPage = new TopicsPage();
        this.consumersPage = new ConsumersPage();

        this.switchLayout = new SwitchLayout("MainPageSwitcher");
        var mainLayout = new FrameLayout("FrameAroundMainSwitch", switchLayout);
        switchLayout.add(topicsPage.getId(), topicsPage.getLayout());
        switchLayout.add(consumersPage.getId(), consumersPage.getLayout());
        return mainLayout;
    }


    public enum Operation {
        UP,
        EXIT,
        SEARCH,
        DOWN,
        NONE,
        SWITCH_TO_TOPICS,
        SWITCH_TO_CONSUMER,
        SWITCH_TO_RECORDS
    }


}
