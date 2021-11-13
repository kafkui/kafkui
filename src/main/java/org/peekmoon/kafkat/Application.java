package org.peekmoon.kafkat;

 import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.peekmoon.kafkat.tui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import static org.jline.terminal.TerminalBuilder.PROP_DISABLE_ALTERNATE_CHARSET;

public class Application  {

    static {
        LogInitializer.init();
    }

    private final static Logger log = LoggerFactory.getLogger(Application.class.getName());

    public static void main(String[] args) {

        new Application().run();
    }

    private SwitchLayout switchLayout;
    private TopicsPage topicsPage;
    private ConsumersPage consumersPage;

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

            BlockingQueue<Operation> actionQueue = new LinkedBlockingDeque<>();

            // TODO : Better implementation as the action queue. The display thread should be using the action queue ?
            Thread keyboardThread = new Thread(new KeyboardController(terminal, actionQueue));
            keyboardThread.setDaemon(true);
            keyboardThread.start();

            Thread displayThread = new Thread(display);
            displayThread.setDaemon(true);
            displayThread.start();

            KafkaController kafkaController = new KafkaController();
            kafkaController.update(topicsPage);
            kafkaController.update(consumersPage);


            Page currentPage = topicsPage;
            switchLayout.switchTo("TOPICS");
            boolean askQuit = false;
            while (!askQuit){

                Operation op = actionQueue.take();
                log.info("Receiving an new action {}", op);
                switch (op) {
                    case EXIT -> askQuit = true;
                    case SWITCH_TO_CONSUMER -> {

                        currentPage = consumersPage;
                        switchLayout.switchTo("CONSUMERS");
                    }
                    case SWITCH_TO_TOPICS -> {
                        currentPage = topicsPage;
                        switchLayout.switchTo("TOPICS");
                    }
                }
                currentPage.process(op);
            }


        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    private InnerLayout buildLayout() {
        this.topicsPage = new TopicsPage();
        this.consumersPage = new ConsumersPage();


        this.switchLayout = new SwitchLayout();
        switchLayout.add("TOPICS", topicsPage.getTable());
        switchLayout.add("CONSUMERS", consumersPage.getTable());
        return new FrameLayout(switchLayout);
    }


    public enum Operation {
        UP,
        EXIT,
        SEARCH,
        DOWN,
        NONE,
        SWITCH_TO_TOPICS,
        SWITCH_TO_CONSUMER
    }


}
