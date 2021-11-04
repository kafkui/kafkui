package org.peekmoon.kafkat;

public class ViewLocation<T extends View> {

    private final T view;
    private int x,y;


    public ViewLocation(T view) {
        this.view = view;
        this.x = y = 0;
    }

    public ViewLocation(T view, int x, int y) {
        this.view = view;
        this.x = x;
        this.y = y;
    }


    public T getView() {
        return view;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
