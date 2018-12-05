package com.iwuyc.tools.commons.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * pattern 缓存
 *
 * @author iWuYc
 */
public class PatternCacheUtils {
    private static final Cache<Object, Object> PATTERN_CACHE = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES).maximumSize(1_000).build();

    public static Object getPattern(String string, int flag) {

        try {
            return PATTERN_CACHE.get(string, () -> Pattern.compile(string, flag));
        } catch (ExecutionException e) {
            String msg = "Pattern:" + string + ";Flag:" + flag;
            throw new IllegalArgumentException(msg, e);
        }
    }

    private static class PatternLoader extends CacheLoader<String, Pattern> {
        @Override
        public Pattern load(String key) throws Exception {
            return Pattern.compile(key);
        }
    }
}
