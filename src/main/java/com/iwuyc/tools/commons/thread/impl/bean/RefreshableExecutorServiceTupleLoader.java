package com.iwuyc.tools.commons.thread.impl.bean;

import com.google.common.cache.CacheLoader;

/**
 * 可刷新线程池元组加载类
 *
 * @author Neil
 */
public class RefreshableExecutorServiceTupleLoader extends CacheLoader<String, RefreshableExecutorServiceTuple> {
    @Override
    public RefreshableExecutorServiceTuple load(String domain) throws Exception {
        return new RefreshableExecutorServiceTuple(domain);
    }
}
