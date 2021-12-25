package org.peekmoon.kafkat;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.jline.keymap.KeyMap;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.peekmoon.kafkat.action.Action;
import org.peekmoon.kafkat.action.ExitAction;
import org.peekmoon.kafkat.action.VoidAction;
import org.peekmoon.kafkat.configuration.ClusterConfiguration;
import org.peekmoon.kafkat.tui.*;
import org.peekmoon.kafkat.utils.LogInitializer;
import org.peekmoon.kafkat.utils.UncaughtExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;
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

    private final BlockingQueue<Action> actions = new ArrayBlockingQueue<>(10);
    private Terminal terminal;
    private boolean askQuit = false;
    private KeyboardController keyboardController;
    private SwitchLayout switchLayout;
    private ViewLayout statusMessageLayout;
    private ClustersPage clustersPage;
    private Page currentPage;
    private ProcessingIndicator processingIndicator;

    public void run() {
        log.info("Starting app's");

        // Set a default handler to log all exceptions
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());

        // Workaround terminal with TERM=screen-256color display bad frame border
        System.setProperty(PROP_DISABLE_ALTERNATE_CHARSET, "true");


        try (var terminal = this.terminal = TerminalBuilder.builder().build();
             var display = new Display(terminal, buildLayout())) {

            keyboardController = new KeyboardController(this, terminal, actions);
            Thread keyboardThread = new Thread(keyboardController, "KeyboardThread");
            keyboardThread.setDaemon(true);
            keyboardThread.start();

            Thread displayThread = new Thread(display);
            displayThread.setDaemon(true);
            displayThread.start();

            currentPage = clustersPage;
            switchLayout.switchTo(clustersPage.getId());
            keyboardController.setLocalKeyMap(clustersPage.getKeyMap(terminal));
            clustersPage.activate();
            while (!askQuit){
                // TODO : The display thread should be using the action queue ?
                Action action = actions.take();
                log.info("Receiving an new action {}/{}", action, actions.size());
                action.apply();
            }

        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException(e);
        }

        log.info("Stopping app's");
    }

    public Admin openKafkaAdmin(ClusterConfiguration cluster) {
        Properties config = new Properties();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, cluster.bootstrapServers);
        config.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 5* 1000);
        config.put(AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, 5 * 1000);
        return Admin.create(config);
    }

    public void switchPage(Page page) {
        switchLayout.add(page.getId(), page.getLayout());
        currentPage.deactivate();
        page.activate();
        keyboardController.setLocalKeyMap(page.getKeyMap(terminal));
        switchLayout.switchTo(page.getId());
        currentPage = page;
    }


    public void exit() {
        askQuit = true;
        currentPage.deactivate();
    }

    private InnerLayout buildLayout() {
        this.switchLayout = new SwitchLayout("MainPageSwitcher");

        // Two vertical layout 1/Main content 2/StatusLine
        var mainLayout = new StackVerticalLayout("MainLayout");
        mainLayout.add(new FrameLayout("FrameAroundMainSwitch", switchLayout), StackSizeMode.PROPORTIONAL, 1);
        mainLayout.add(buildStatusLine(), StackSizeMode.SIZED, 1);

        this.clustersPage = new ClustersPage(this);
        switchLayout.add(clustersPage.getId(), clustersPage.getLayout());

        return mainLayout;
    }

    // Status line = 2 horizontals : indicator + message
    private InnerLayout buildStatusLine() {
        statusMessageLayout = new ViewLayout("StatusLineMessage");
        var statusLine = new StackHorizontalLayout("StatusLine");
        processingIndicator = new ProcessingIndicator();
        statusLine.add(processingIndicator.getLayout(), StackSizeMode.SIZED, 2);
        statusLine.add(statusMessageLayout, StackSizeMode.PROPORTIONAL, 1);
        return statusLine;
    }


    public KeyMap<Action> buildKeyMap() {
        KeyMap<Action> keyMap = new KeyMap<>();
        keyMap.setAmbiguousTimeout(100);
        keyMap.setNomatch(new VoidAction());
        keyMap.bind(new ExitAction(this), "q", KeyMap.esc() );
        //keyMap.bind(new SwitchToPageAction(this, consumersPage), ":c");
        return keyMap;
    }

    public void status(String msg) {
        statusMessageLayout.putItem("0", msg);
    }

    public void clearStatus() {
        statusMessageLayout.removeItem("0");
    }

    public ProcessingIndicator getProcessingIndicator() {
        return processingIndicator;
    }


}
