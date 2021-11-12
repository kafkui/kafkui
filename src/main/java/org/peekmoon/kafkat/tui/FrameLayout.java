package org.peekmoon.kafkat.tui;

import org.jline.utils.AttributedStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrameLayout extends InnerLayout {

    private final static Logger log = LoggerFactory.getLogger(FrameLayout.class);

    private static final String HORIZONTAL_CHAR = "─"; // 0x2500
    private static final String VERTICAL_CHAR =  "│";// 0x2502
    private static final String TOP_LEFT_CHAR =  "┌"; //  0x250C
    private static final String TOP_RIGNT_CHAR =  "┐"; // 0x2510
    private static final String BOTTOM_LEFT_CHAR =  "└"; // 0x2514
    private static final String BOTTOM_RIGHT_CHAR =  "┘"; // 0x2518


    private final InnerLayout inner;

    public FrameLayout(InnerLayout inner) {
        this.inner = inner;
        inner.setParent(this);
    }

    @Override
    public int getWidth() {
        return inner.getWidth() + 2;
    }

    @Override
    public int getHeight() {
        return inner.getHeight() + 2;
    }

    @Override
    public void resize(int width, int height) {
        inner.resize(width-2, height-2);
        log.debug("Resized : {}", this);
    }


    @Override
    public AttributedStringBuilder render(int y) {
        if (y == 0) {
            return new AttributedStringBuilder()
                    .append(TOP_LEFT_CHAR)
                    .append(HORIZONTAL_CHAR.repeat(getWidth()-2))
                    .append(TOP_RIGNT_CHAR);
        }
        if (y == getHeight()-1) {
            return new AttributedStringBuilder()
                    .append(BOTTOM_LEFT_CHAR)
                    .append(HORIZONTAL_CHAR.repeat(getWidth()-2))
                    .append(BOTTOM_RIGHT_CHAR);
        }
        return new AttributedStringBuilder().append(VERTICAL_CHAR).append(inner.render(y-1)).append(VERTICAL_CHAR);
    }

    @Override
    public String toString() {
        return super.toString() + " over [" + inner.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + "]";
    }
}
