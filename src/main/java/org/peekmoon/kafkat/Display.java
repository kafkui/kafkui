package org.peekmoon.kafkat;

import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.jline.utils.InfoCmp.Capability.*;

public class Display implements Runnable, Closeable {

    private static final Logger log = LoggerFactory.getLogger(Display.class);

    private final Terminal terminal;
    private final AtomicBoolean askStop;
    private final BlockingQueue invalidate;
    private final Terminal.SignalHandler prevWinchHandler;
    private final Attributes savedAttributes;
    private final org.jline.utils.Display display;

    private final Size size = new Size();
    private View view;

    public Display(Terminal terminal) {
        this.terminal = terminal;
        this.askStop = new AtomicBoolean(false);
        this.invalidate = new LinkedBlockingQueue(1);

        this.display = new org.jline.utils.Display(terminal, true);

        prevWinchHandler = terminal.handle(Terminal.Signal.WINCH, this::resize);
        savedAttributes = terminal.enterRawMode();
        terminal.puts(cursor_invisible);
        terminal.puts(enter_ca_mode);
        terminal.puts(keypad_xmit);
        terminal.puts(cursor_invisible);
        terminal.flush();

        resize(Terminal.Signal.WINCH);
    }

    private synchronized void resize(Terminal.Signal signal) {
        log.info("resize");
        size.copy(terminal.getSize());
        display.resize(size.getRows(), size.getColumns());
        display.clear();
        invalidate();
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


//
//
//            display.clear();
//            display.update(Collections.emptyList(), terminal.getSize().cursorPos(10,10));
//            display.


    @Override
    public void run() {
        try {
            while (!askStop.get()) {
                if (invalidate.poll(200, TimeUnit.MILLISECONDS) != null) {
                    log.info("starting draw");
                    synchronized (this) {
                        log.debug("aquired");
                        List<AttributedString> lines = render();
                        display.update(lines, 0);
                    }
                }
            }
        } catch(InterruptedException e){
            log.info("Display asked to stop", e);
        }

    }

    private List<AttributedString> render() {

        return view.render();
    }

    @Override
    public void close() throws IOException {
        log.info("Closing");
        askStop.set(true);
        terminal.setAttributes(savedAttributes);
        terminal.handle(Terminal.Signal.WINCH, prevWinchHandler);
        terminal.puts(exit_ca_mode);
        terminal.puts(keypad_local);
        terminal.puts(cursor_normal);
        terminal.flush();
        terminal.close();
    }



    public void add(View view) {
        this.view = view;
    }

    // Use a queue allow to have only one drawing at a time and aggragate all ask
    public void invalidate() {
        invalidate.offer("dummy"); // TODO : Think to a better event management
    }


    public int getWidth() {
        return size.getColumns();
    }

    public int getHeight() {
        return size.getRows();
    }
}
