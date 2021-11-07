package org.peekmoon.kafkat.tui;

import org.jline.utils.AttributedStringBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class StackLayout extends InnerLayout {

    private List<InnerLayout> inners = new ArrayList<>();

    @Override
    public int getWidth() {
        return inners.stream().mapToInt(Layout::getWidth).sum();
    }

    @Override
    public int getHeight() {
        return inners.stream().mapToInt(Layout::getHeight).max().getAsInt();
    }

    @Override
    public void resize(int width, int height) {
        // TODO : For now only all same size
        // TODO : For now only horizontal stack
        // TODO : For now all inners of same size
        int innerWidth = width / inners.size();
        inners.forEach(l -> l.resize(innerWidth , height));
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