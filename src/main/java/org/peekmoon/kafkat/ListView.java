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

    public ListView(Display display, int w, int h) {
        super(display, w, h);
        this.selected = null;
    }

    public void addAll(Collection<String> values) {
        for (String value : values) {
            this.values.add(value);
        }
        if (selected == null) { selected = 0;}
        invalidate();
    }

    public void add(String value) {
        if (selected == null) { selected = 0;}
        values.add(value);
        invalidate();
    }

    public void selectUp() {
        selected--;
        invalidate();
    }

    public void selectDown() {
        selected++;
        invalidate();
    }

    @Override
    public List<AttributedString> render() {
        List<AttributedString> result = new ArrayList<>();
        for (int i = 0; i< getHeight(); i++) {
            result.add(buildLine(i));
        }
        return result;
    }

    public AttributedString buildLine(int i) {
        AttributedStringBuilder builder = new AttributedStringBuilder(getWidth());
        AttributedStyle style = (Objects.equals(i, selected) ? AttributedStyle.INVERSE : AttributedStyle.DEFAULT);
        builder.style(style);
        String value = i<values.size() ? values.get(i) : "";
        // FIXME : If value len is greater as width
        builder
                .append(value)
                .append(" ".repeat(getWidth() - value.length()));
        return builder.toAttributedString();
    }

    public int size() { // FixME for scrolling
        return values.size();
    }
}
