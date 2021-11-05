package org.peekmoon.kafkat;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

public class Application  {

    static {
        LogInitializer.init();
    }

    private final static Logger log = LoggerFactory.getLogger(Application.class.getName());

    public static void main(String[] args) throws Exception {

        log.info("Starting app's");

        // Set a default handler to log all exceptions
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            org.slf4j.Logger log = LoggerFactory.getLogger(Application.class);
            log.error("Uncaugh error", e);
        });


        try (Terminal terminal = TerminalBuilder.builder().build();
             Display display = new Display(terminal)) {

            KafkaController kafkaController = new KafkaController();

            BlockingQueue<Operation> actionQueue = new LinkedBlockingDeque<>();
            BlockingQueue<Void> invalidate = new LinkedBlockingDeque<>(1);

            TopcisView model = new TopcisView(display, 20, 5);
            kafkaController.update(model);

            display.add(model);


            Thread t = new Thread(new KeyboardController(terminal, actionQueue));
            t.setDaemon(true);
            t.start();

            Thread displayThread = new Thread(display);
            displayThread.start();


            boolean askQuit = false;
            while (!askQuit){

                Operation op = actionQueue.take();
                log.info("Receiving an new action {}", op);
                switch (op) {
                    case EXIT -> askQuit = true;
                    case UP -> model.selectUp();
                    case DOWN -> model.selectDown();
                }
            }


        } catch (IOException  e) {
            throw new IllegalStateException(e);
        }

            //var topics = admin.describeTopics(topicsList).all().get();


//            for (Node node : admin.describeCluster().nodes().get()) {
//                writer.print("-- node: " + node.id() + " --");
//                ConfigResource cr = new ConfigResource(ConfigResource.Type.BROKER, "0");
//                DescribeConfigsResult dcr = admin.describeConfigs(Collections.singleton(cr));
//                dcr.all().get().forEach((k, c) -> {
//                    c.entries()
//                            .forEach(configEntry -> {System.out.println(configEntry.name() + "= " + configEntry.value());});
//                });


//            terminal.flush();
    }



    private static void buildTestTopics(AdminClient admin) throws InterruptedException, ExecutionException {
        //admin.deleteTopics(testTopics().stream().map(t->t.name()).collect(Collectors.toList())).all().get();
        Set<String> existingTopics = admin.listTopics().names().get();
        var topicsToBuild = testTopics().stream().filter(n->!existingTopics.contains(n.name())).collect(Collectors.toList());
        var topicResult = admin.createTopics(topicsToBuild);
        topicResult.all().get();
    }

    private static Collection<NewTopic> testTopics() {
        return List.of(
                new NewTopic("simple", 1, (short) 1),
                new NewTopic("cities", 10, (short) 3),
                new NewTopic("blabla.envet", 3, (short) 3),
                new NewTopic("supercharger", 1, (short) 2),
                new NewTopic("castratorine", 10, (short) 2),
                new NewTopic("glouton", 25, (short) 5)
                );
    }

    public enum Operation {
        UP,
        EXIT,
        SEARCH, DOWN, NONE
    }


}
