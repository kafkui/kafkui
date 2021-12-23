package org.peekmoon.kafkat.tui;

import org.jline.utils.AttributedStringBuilder;

public interface Layout {

     int getWidth();
     int getHeight();
     void resize(int width, int height);
     AttributedStringBuilder render(int y);
     void invalidate(boolean resizing);
     String getName(); // Used for debug purpose

}
