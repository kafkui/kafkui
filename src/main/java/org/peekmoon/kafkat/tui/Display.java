package org.peekmoon.kafkat.tui;

import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.jline.utils.InfoCmp.Capability.*;

public class Display implements Runnable, Closeable {

    private static final Logger log = LoggerFactory.getLogger(Display.class);

    private enum Event {
        REDRAW,
        RESIZE_AND_REDRAW,
        STOP
    }

    private final Terminal terminal;
    private final BlockingQueue<Event> events;
    private final Terminal.SignalHandler prevWinchHandler;
    private final Attributes savedAttributes;
    private final org.jline.utils.Display display;

    private final Size size = new Size();
    private final RootLayout layout;

    public Display(Terminal terminal, InnerLayout layout) {
        this.terminal = terminal;
        this.layout = new RootLayout(this, layout);

        this.events = new LinkedBlockingQueue<>(100);

        this.display = new org.jline.utils.Display(terminal, true);

        prevWinchHandler = terminal.handle(Terminal.Signal.WINCH, signal -> invalidate(true));
        savedAttributes = terminal.enterRawMode();
        terminal.puts(cursor_invisible);
        terminal.puts(enter_ca_mode);
        terminal.puts(keypad_xmit);
        terminal.flush();
        invalidate(true);
    }

    @Override
    public void run() {
        try {
            Event event;
            do {
                event = events.take();
                log.debug("Event : {}/{}", event, events.size());
                synchronized (this) {
                    switch (event) {
                        case RESIZE_AND_REDRAW -> {
                            resize();
                            redraw();
                        }
                        case REDRAW -> redraw();
                    }
                    log.debug("Event done");
                }
            } while (event != Event.STOP);

        } catch(InterruptedException e){
            log.info("Display loop interrupted", e);
        }
    }


    private void resize() {
        log.info("Resizing");
        size.copy(terminal.getSize());
        display.resize(size.getRows(), size.getColumns());
        layout.resize(size.getColumns(), size.getRows());
        log.info("Resize done");
    }


    private void redraw() {
        display.reset(); // FIXME : Workaround : https://github.com/jline/jline3/issues/737
        List<AttributedString> lines = new ArrayList<>();
        for (int y=0; y<layout.getHeight(); y++) {
            lines.add(layout.render(y).toAttributedString());
        }
        display.update(lines, 0);
    }

    @Override
    public void close() throws IOException {
        log.info("Closing");
        events.add(Event.STOP);
        terminal.setAttributes(savedAttributes);
        terminal.handle(Terminal.Signal.WINCH, prevWinchHandler);
        terminal.puts(exit_ca_mode);
        terminal.puts(keypad_local);
        terminal.puts(cursor_normal);
        terminal.flush();
        terminal.close();
    }


    public void invalidate(boolean resizing) {
        synchronized (events) {
            if (resizing) {
                if (!events.contains(Event.RESIZE_AND_REDRAW)) {
                    events.add(Event.RESIZE_AND_REDRAW);
                }
                // Removing all remaining REDRAW event from queue, because they becoming useless
                events.removeIf(e -> e == Event.REDRAW) ;

            } else { // Only a redraw
                // If queue contain an resize and redraw or already contains a redraw no need to redraw now
                if (!events.contains(Event.RESIZE_AND_REDRAW) && !events.contains(Event.REDRAW)) {
                    events.add(Event.REDRAW);
                }
            }
        }
    }


    public int getWidth() {
        return size.getColumns();
    }

    public int getHeight() {
        return size.getRows();
    }
}
