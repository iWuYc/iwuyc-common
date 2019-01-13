package com.iwuyc.tools.commons.util.xml;

public interface Parser<S, R> {
    void parser(S element);

    R result();
}
