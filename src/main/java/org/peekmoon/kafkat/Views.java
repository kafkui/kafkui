package org.peekmoon.kafkat;

import org.jline.utils.AttributedString;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Views<T extends View> extends View {

    private final List<ViewLocation<T>> views = new ArrayList<>();

    public Views(Display display, int w, int h) {
        super(display, w, h);
    }

    public void add(T view, int x, int y) {
        views.add(new ViewLocation<>(view, x, y));
    }

    public int size() {
        return views.size();
    }


    @Override
    public List<AttributedString> render() {

        ViewRenderer renderer = new ViewRenderer(this);
        views.forEach(v -> renderer.add(v));

        return renderer.render();
    }

    public void forEach(Consumer<T> consumer) {
        views.stream().map(v -> v.getView()).forEach(consumer);
    }

    public Stream<T> stream() {
        return views.stream().map(v -> v.getView());
    }

    public T get(int viewPos) {
        return views.get(viewPos).getView();
    }
}
