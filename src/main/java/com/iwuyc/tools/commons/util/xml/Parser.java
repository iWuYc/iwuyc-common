package com.iwuyc.tools.commons.util.xml;

import com.google.gson.JsonElement;
import org.dom4j.Node;

public interface Parser<S extends Node,T extends JsonElement> {
    void parser(S element);
}
