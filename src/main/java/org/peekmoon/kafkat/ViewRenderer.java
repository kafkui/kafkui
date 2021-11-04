package org.peekmoon.kafkat;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ViewRenderer {

    private final AttributedStringBuilder[] sequences;

    public ViewRenderer(Display display) {
        this(display.getWidth(), display.getHeight());
    }

    public ViewRenderer(View view) {
        this(view.getWidth(), view.getHeight());
    }

    public ViewRenderer(int width, int height) {
        this.sequences = new AttributedStringBuilder[height];
        var emptyLine = new AttributedStringBuilder().append(" ".repeat(width)) ;
        Arrays.setAll(sequences, i -> emptyLine);
    }

    // TODO: Check boundary ?
    public void add(ViewLocation viewLocation) {
        var toMerge = viewLocation.getView().render();
        for (int row = 0; row < viewLocation.getView().getHeight(); row++) {  // For all view line
            AttributedStringBuilder oldSequence = sequences[row + viewLocation.getY()];
            var newSequence = new AttributedStringBuilder()
                    .append(oldSequence, 0, viewLocation.getX() )
                    .append(toMerge.get(row))
                    .append(oldSequence, viewLocation.getX() + viewLocation.getView().getWidth() + 1, oldSequence.length());
            sequences[row + viewLocation.getY()] = newSequence;
        }
    }

    public List<AttributedString> render() {
        return Arrays.stream(sequences)
                .map(s -> s.toAttributedString())
                .collect(Collectors.toList());
    }

}
