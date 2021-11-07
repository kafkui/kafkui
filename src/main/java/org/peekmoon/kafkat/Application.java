package org.peekmoon.kafkat;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.peekmoon.kafkat.tui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadLocalRandom;
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
             Display display = new Display(terminal, buildLayout())) {

            KafkaController kafkaController = new KafkaController();

            BlockingQueue<Operation> actionQueue = new LinkedBlockingDeque<>();


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
                    case UP -> table.selectUp();
                    case DOWN -> table.selectDown();
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

    static Table table;

    private static InnerLayout buildLayout() {
        ViewLayout leftView = new ViewLayout();
//        leftView.addItem("Test1");
//        leftView.addItem("Test2 plu long");
//        leftView.addItem("T");
//        leftView.addItem("");
//        leftView.addItem("*****************************************|");
//        leftView.addItem("123456");
        for (int i=0; i<20;i++) {
            leftView.addItem(String.format("%2d: ", i) + UUID.randomUUID());
        }
        leftView.addItem("-----------");


        ViewLayout rightView = new ViewLayout();
        rightView.addItem("Right View");
        for (int i=0; i<20;i++) {
            rightView.addItem("Repeat after me!!é&");
        }

        ViewLayout rrightView = new ViewLayout();
        rrightView.addItem("Exterme View");
        for (int i=0; i<20;i++) {
            rrightView.addItem(String.format("%2d: ", i) + UUID.randomUUID());
        }


//        stack.add(leftView);
//        stack.add(rightView);
//        stack.add(rrightView);


        table = new Table();
        table.addColumn("Id");
        table.addColumn("name");
        table.addColumn("status");
        for (int i=0; i<66;i++) {
            table.addRow(
                    String.format("<%2d: ", i) + UUID.randomUUID() + ">",
                    "<" + "bla".repeat(ThreadLocalRandom.current().nextInt(6) + 1) + ">",
                    String.format("<%2d: A droite : ", i) + UUID.randomUUID() + ">"
            );
        }


        // TODO : Should always be included in a root ?
        //scrollLayout = new ScrollLayout(table.getLayout());

        return table;
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
