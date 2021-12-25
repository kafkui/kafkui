package org.peekmoon.kafkat;

import org.peekmoon.kafkat.tui.InnerLayout;
import org.peekmoon.kafkat.tui.ViewLayout;

import java.util.Timer;
import java.util.TimerTask;

public class ProcessingIndicator extends TimerTask {
    private static final String[] animation = {"\u25E7", "\u25E9", "\u2B12", "\u2B14", "\u25E8", "\u25EA", "\u2B13", "\u2B15"};

    private final ViewLayout layout;
    private final Timer timer;
    private boolean animating = false;
    private int currentChar = 0;


    public ProcessingIndicator() {
        this.layout = new ViewLayout("ProcessingIndicator");
        timer = new Timer("anim_process_indicator", true);
        timer.schedule(this, 0, 300);
    }

    InnerLayout getLayout() {
        return layout;
    }

    // Start the processing indicator only if asking page is the current active page
    public synchronized void start(Page askingPage){
        if (askingPage.isActive()) {
            animating = true;
            layout.putItem("0", " ");
        }
    }

    // Stop the processing indicator only if asking page is the current active page
    public synchronized void stop(Page askingPage) {
        if (askingPage.isActive()) {
            animating = false;
            layout.removeItem("0");
        }
    }

    @Override
    public synchronized void run() {
        if (animating) {
            layout.putItem("0", animation[currentChar]);
            currentChar = ++currentChar % animation.length;
        }
    }
}
