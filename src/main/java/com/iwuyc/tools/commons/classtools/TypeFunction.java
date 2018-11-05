package com.iwuyc.tools.commons.classtools;

import java.io.Serializable;
import java.util.function.Function;

public interface TypeFunction<T, R> extends Serializable, Function<T, R> {
}
