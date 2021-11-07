package org.peekmoon.kafkat.tui;

import org.jline.utils.AttributedStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class StackLayout extends InnerLayout {

    private final static Logger log = LoggerFactory.getLogger(StackLayout.class);

    private List<InnerLayout> inners = new ArrayList<>();

    @Override
    public int getWidth() {
        return inners.stream().mapToInt(Layout::getWidth).sum();
    }

    @Override
    public int getHeight() {
        return inners.stream().mapToInt(Layout::getHeight).max().orElse(0);
    }

    @Override
    public void resize(int width, int height) {
        // TODO : For now only all same size
        // TODO : For now only horizontal stack
        int innerWidth = width / inners.size();
        inners.forEach(l -> l.resize(innerWidth , height));
        log.debug("Resized : {}", this);
    }

    @Override
    public AttributedStringBuilder render(int y) {
        AttributedStringBuilder builder = new AttributedStringBuilder();
        inners.forEach(l -> builder.append(buildLine(l, y)));
        return builder;
    }

    private AttributedStringBuilder buildLine(InnerLayout inner, int y) {
        return y < inner.getHeight() ? inner.render(y) : emptyLine(inner.getWidth());
    }


    public void add(InnerLayout innerLayout) {
        innerLayout.setParent(this);
        inners.add(innerLayout);
    }


}
