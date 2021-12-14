package org.peekmoon.kafkat;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.jline.keymap.KeyMap;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.peekmoon.kafkat.action.Action;
import org.peekmoon.kafkat.action.ExitAction;
import org.peekmoon.kafkat.action.SwitchToPageAction;
import org.peekmoon.kafkat.action.VoidAction;
import org.peekmoon.kafkat.configuration.ClusterConfiguration;
import org.peekmoon.kafkat.configuration.Configuration;
import org.peekmoon.kafkat.tui.Display;
import org.peekmoon.kafkat.tui.FrameLayout;
import org.peekmoon.kafkat.tui.InnerLayout;
import org.peekmoon.kafkat.tui.SwitchLayout;
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

        new Application().run(Configuration.read());
    }

    private Terminal terminal;
    private Admin kafkaAdmin;
    private boolean askQuit = false;
    private BlockingQueue<Action> actions = new ArrayBlockingQueue<>(10);
    private KeyboardController keyboardController;
    private SwitchLayout switchLayout;
    private ClustersPage clustersPage;
    private ConsumersPage consumersPage;
    private Page currentPage;

    public void run(Configuration configuration) {

        log.info("Starting app's");

        // Set a default handler to log all exceptions
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());

        // Workaround terminal with TERM=screen-256color display bad frame border
        System.setProperty(PROP_DISABLE_ALTERNATE_CHARSET, "true");


        try (var ignored  = openKafkaAdmin(configuration.clusters.get(0));
             var terminal = this.terminal = TerminalBuilder.builder().build();
             var display = new Display(terminal, buildLayout(configuration))) {

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
            currentPage.deactivate();

        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException(e);
        }

        log.info("Stopping app's");
    }

    public Admin openKafkaAdmin(ClusterConfiguration cluster) {
        Properties config = new Properties();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, cluster.bootstrapServers);
        return kafkaAdmin = Admin.create(config);
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
    }

    private InnerLayout buildLayout(Configuration configuration) {
        this.consumersPage = new ConsumersPage(this);
        this.clustersPage = new ClustersPage(this, configuration.clusters);
        this.switchLayout = new SwitchLayout("MainPageSwitcher");
        var mainLayout = new FrameLayout("FrameAroundMainSwitch", switchLayout);
        switchLayout.add(consumersPage.getId(), consumersPage.getLayout());
        switchLayout.add(clustersPage.getId(), clustersPage.getLayout());
        return mainLayout;
    }




    public KeyMap<Action> buildKeyMap() {
        KeyMap<Action> keyMap = new KeyMap<>();
        keyMap.setAmbiguousTimeout(100);
        keyMap.setNomatch(new VoidAction());
        keyMap.bind(new ExitAction(this), "q", KeyMap.esc() );
        keyMap.bind(new SwitchToPageAction(this, consumersPage), ":c");
        return keyMap;
    }

    public Admin getKafkaAdmin() {
        return kafkaAdmin;
    }
}
