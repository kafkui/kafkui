package org.peekmoon.kafkat.tui;

import org.peekmoon.kafkat.Application;

public interface Page {
    void process(Application.Operation op);
}