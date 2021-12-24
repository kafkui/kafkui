package org.peekmoon.kafkat.tui;

public enum StackSizeMode {
    SIZED,        // Fixed size provided at creation time
    CONTENT,      // Fixed size based on content -> need a resize event to adjust
    PROPORTIONAL  // Share the remaining space with a coefficient
}
