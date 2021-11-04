package org.peekmoon.kafkat;

import org.jline.utils.AttributedString;

import java.util.List;

public abstract class View {

    protected final Display display;

    private int width, height;

    public View(Display display, int width, int height) {
        this.display = display;
        this.width = width;
        this.height = height;
    }

    public abstract List<AttributedString> render();

    protected void invalidate() {
        this.display.invalidate();
    };

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
