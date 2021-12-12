package org.peekmoon.kafkat;

public interface Page {
    void activate();
    void deactivate();
    void process(Application.Operation op);
}
