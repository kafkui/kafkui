package org.peekmoon.kafkat;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class ListView extends View {

    private final List<String> values = new ArrayList<>(); // TODO : Replace String with better ??
    private Integer selected;
    private int offset;

    public ListView(Display display, int w, int h) {
        super(display, w, h);
        this.selected = null;
        this.offset = 0;
    }

    public void addAll(Collection<String> values) {
        this.values.addAll(values);
        if (selected == null) { selected = 0;}
        invalidate();
    }

    public void add(String value) {
        if (selected == null) { selected = 0;}
        values.add(value);
        invalidate();
    }

    public void selectUp() {
        boolean invalidate = false;
        if (selected > 0) {
            selected--;
            invalidate = true;
        }
        if (selected  < offset) {
            offset = selected;
            invalidate = true;
        }
        if (invalidate) invalidate();

    }

    public void selectDown() {
        boolean invalidate = false;
        if (selected + 1 < values.size()) {
            selected++;
            invalidate = true;
        }
        if (selected  + offset >= getHeight() && offset + getHeight() < values.size()) {
            offset++;
            invalidate = true;
        }
        if (invalidate) invalidate();

    }

    @Override
    public List<AttributedString> render() {
        List<AttributedString> result = new ArrayList<>();
        for (int i = 0; i< getHeight(); i++) {
            result.add(buildLine(i));
        }
        return result;
    }

    public AttributedString buildLine(int noLine) {
        AttributedStringBuilder builder = new AttributedStringBuilder(getWidth());
        AttributedStyle style = (Objects.equals(noLine + offset, selected) ? AttributedStyle.INVERSE : AttributedStyle.DEFAULT);
        builder.style(style);
        String value = (noLine + offset < values.size()) ? values.get(noLine + offset) : "";
        // FIXME : If value len is greater as width
        builder
                .append(value)
                .append(" ".repeat(getWidth() - value.length()));
        return builder.toAttributedString();
    }


}
