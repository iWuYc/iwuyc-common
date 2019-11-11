package com.iwuyc.tools.commons.thread.impl.bean;

import com.google.common.cache.CacheLoader;
import com.iwuyc.tools.commons.basic.collections.ConcurrentHashSet;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * @author Neil
 */
public class RefreshableExecutorServiceTuplesLoader extends CacheLoader<JDKExecutorServiceTuple, Collection<RefreshableExecutorServiceTuple>> {
    @Override
    public Collection<RefreshableExecutorServiceTuple> load(@Nonnull JDKExecutorServiceTuple key) {
        return new ConcurrentHashSet<>();
    }


}