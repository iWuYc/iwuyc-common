package com.iwuyc.tools.commons.util.xml;

public interface Parser<S, R> {
    R parser(S element);

}
