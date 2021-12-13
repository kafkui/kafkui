package org.peekmoon.kafkat;

import org.jline.keymap.KeyMap;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.peekmoon.kafkat.tui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.jline.terminal.TerminalBuilder.PROP_DISABLE_ALTERNATE_CHARSET;

public class Application  {

    static {
        LogInitializer.init();
    }

    private final static Logger log = LoggerFactory.getLogger(Application.class.getName());


    public static void main(String[] args) {

        new Application().run();
    }

    private Terminal terminal;
    private boolean askQuit = false;
    private BlockingQueue<Action> actions = new ArrayBlockingQueue<>(10);
    private KeyboardController keyboardController;
    private SwitchLayout switchLayout;
    private TopicsPage topicsPage;
    private ConsumersPage consumersPage;
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


        try (Terminal terminal = this.terminal = TerminalBuilder.builder().build();
             Display display = new Display(terminal, buildLayout())) {

            keyboardController = new KeyboardController(this, terminal, actions);
            Thread keyboardThread = new Thread(keyboardController, "KeyboardThread");
            keyboardThread.setDaemon(true);
            keyboardThread.start();

            Thread displayThread = new Thread(display);
            displayThread.setDaemon(true);
            displayThread.start();

            currentPage = topicsPage;
            switchLayout.switchTo(topicsPage.getId());
            keyboardController.setLocalKeyMap(topicsPage.getKeyMap(terminal));
            topicsPage.activate();
            while (!askQuit){
                // TODO : The display thread should be using the action queue ?
                Action action = actions.take();
                log.info("Receiving an new action {}/{}", action, actions.size());
                action.apply();
            }
            currentPage.deactivate();

        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    void switchPage(Page page) {
        switchLayout.add(page.getId(), page.getLayout());
        currentPage.deactivate();
        page.activate();
        keyboardController.setLocalKeyMap(page.getKeyMap(terminal));
        switchLayout.switchTo(page.getId());
        currentPage = page;
    }


    void exit() {
        askQuit = true;
    }

    private InnerLayout buildLayout() {
        this.topicsPage = new TopicsPage(this);
        this.consumersPage = new ConsumersPage(this);

        this.switchLayout = new SwitchLayout("MainPageSwitcher");
        var mainLayout = new FrameLayout("FrameAroundMainSwitch", switchLayout);
        switchLayout.add(topicsPage.getId(), topicsPage.getLayout());
        switchLayout.add(consumersPage.getId(), consumersPage.getLayout());
        return mainLayout;
    }


    public KeyMap<Action> buildKeyMap() {
        KeyMap<Action> keyMap = new KeyMap<>();
        keyMap.setAmbiguousTimeout(100);
        keyMap.setNomatch(new VoidAction());
        keyMap.bind(new ExitAction(this), "q", KeyMap.esc() );
        keyMap.bind(new SwitchToPageAction(this, consumersPage), ":c");
        keyMap.bind(new SwitchToPageAction(this, topicsPage), ":t");
        return keyMap;
    }

    public TopicsPage getTopicsPage() {
        return topicsPage;
    }
}
