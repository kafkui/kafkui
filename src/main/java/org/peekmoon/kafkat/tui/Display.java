package org.peekmoon.kafkat.tui;

import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedCharSequence;
import org.jline.utils.AttributedString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
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
    private final BlockingQueue<String> invalidate;
    private final Terminal.SignalHandler prevWinchHandler;
    private final Attributes savedAttributes;
    private final org.jline.utils.Display display;

    private final Size size = new Size();
    private final RootLayout layout;

    public Display(Terminal terminal, InnerLayout layout) {
        this.terminal = terminal;
        this.layout = new RootLayout(this, layout);

        this.askStop = new AtomicBoolean(false);
        this.invalidate = new LinkedBlockingQueue<>(1);

        this.display = new org.jline.utils.Display(terminal, true);

        prevWinchHandler = terminal.handle(Terminal.Signal.WINCH, this::resize);
        savedAttributes = terminal.enterRawMode();
        terminal.puts(cursor_invisible);
        terminal.puts(enter_ca_mode);
        terminal.puts(keypad_xmit);
        terminal.flush();

        resize(Terminal.Signal.WINCH);
    }

    private synchronized void resize(Terminal.Signal signal) {
        log.info("Resizing");
        size.copy(terminal.getSize());
        display.resize(size.getRows(), size.getColumns());
        display.clear();
        layout.resize(size.getColumns(), size.getRows());
        invalidate();
        log.info("Resize done");
    }

    @Override
    public void run() {
        try {
            while (!askStop.get()) {
                if (invalidate.poll(200, TimeUnit.MILLISECONDS) != null) {
                    synchronized (this) {
                        log.info("Drawing...");
                        display.reset(); // FIXME : Workaround : https://github.com/jline/jline3/issues/737
                        List<AttributedString> lines = render();
                        display.update(lines, 0);
                        log.info("Draw done");
                    }
                }
            }
        } catch(InterruptedException e){
            log.info("Display asked to stop", e);
        }

    }

    private List<AttributedString> render() {
        List<AttributedString> result = new ArrayList<>();
        for (int y=0; y<layout.getHeight(); y++) {
            result.add(layout.render(y).toAttributedString());
        }
        return result;
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


    // Use a queue allow to have only one drawing at a time and aggragate all requests
    // TODO : Thinks to a less hacky implementation
    public void invalidate() {
        //noinspection ResultOfMethodCallIgnored
        invalidate.offer("dummy");
    }


    public int getWidth() {
        return size.getColumns();
    }

    public int getHeight() {
        return size.getRows();
    }
}
