package org.peekmoon.kafkat.tui;

import org.jline.utils.AttributedCharSequence;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Renderer {

    private final int width, height;
    private final AttributedStringBuilder[] sequences;


    public Renderer(int width, int height) {
        this.width = width;
        this.height = height;
        var emptyLine = new AttributedStringBuilder().append(" ".repeat(width)) ;
        this.sequences = new AttributedStringBuilder[height];
        Arrays.fill(sequences, emptyLine);
    }

    public void add(InnerLayout layout) {
//        for (int row = 0; row < layout.getHeight(); row++) {  // For all line in layout
//            AttributedStringBuilder oldSequence = sequences[row + view.getY()];
//            var newSequence = new AttributedStringBuilder()
//                    .append(oldSequence, 0, view.getX() )
//                    .append(toMerge.get(row))
//                    .append(oldSequence, view.getX() + view.getWidth() + 1, oldSequence.length());
//            sequences[row + view.getY()] = newSequence;
//        }
    }

    public void setText(int x, int y, AttributedCharSequence string) {

    }

    public List<AttributedString> render() {
        return Arrays.stream(sequences)
                .map(AttributedCharSequence::toAttributedString)
                .collect(Collectors.toList());
    }


}
