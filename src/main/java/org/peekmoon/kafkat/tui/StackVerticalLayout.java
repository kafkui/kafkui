package org.peekmoon.kafkat.tui;

import org.jline.utils.AttributedStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class StackVerticalLayout extends InnerLayout {

    private final static Logger log = LoggerFactory.getLogger(StackVerticalLayout.class);

    private List<InnerLayout> inners = new ArrayList<>();

    @Override
    public int getWidth() {
        return inners.stream().mapToInt(Layout::getWidth).max().orElse(0);
    }

    @Override
    public int getHeight() {
        return inners.stream().mapToInt(Layout::getHeight).sum();
    }

    @Override
    public void resize(int width, int height) {
        log.debug("Resizing : {} to {},{}", this, width, height);

        // Fixé à la taille du contenu avec optionnellement un min et/ou un max
        // Fixe à un taille = taille du contenu avec min = max
        // Calcul la taille restante
        // Partage avec un coeff entre ce qui reste


        int innerHeight = height / inners.size();
        inners.forEach(l -> l.resize(width, innerHeight));
        log.debug("Resized : {}", this);
    }

    @Override
    public AttributedStringBuilder render(int y) {
        int offsetY = 0;
        for (InnerLayout inner : inners) {
            if (y < offsetY + inner.getHeight()) {
                return inner.render(y - offsetY);
            }
            offsetY += inner.getHeight();
        }
        throw new IllegalArgumentException("Unable to render line " + y + " max ofs : " + offsetY + " in " + this);
    }

    public void add(InnerLayout innerLayout) {
        innerLayout.setParent(this);
        inners.add(innerLayout);
    }


}
