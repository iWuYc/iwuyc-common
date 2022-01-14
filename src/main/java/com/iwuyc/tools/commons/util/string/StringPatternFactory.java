package com.iwuyc.tools.commons.util.string;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class StringPatternFactory {
    private static final LoadingCache<PatternInfo, Pattern> PATTERN_CACHE = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.DAYS).build(new PatternCacheLoader());

    /**
     * @param pattern 正则表达式
     * @return 正则表达式实例
     */
    public static Pattern compile(String pattern) {
        return compile(pattern, 0);
    }

    /**
     * @param pattern 正则表达式
     * @param flag    匹配模式，请使用 {@link Pattern}类中的常量
     * @return 正则表达式实例
     */
    public static Pattern compile(String pattern, int flag) {
        final PatternInfo patternInfo = PatternInfo.builder().flag(flag).pattern(pattern).build();
        return PATTERN_CACHE.getUnchecked(patternInfo);
    }

    @Data
    @EqualsAndHashCode
    @Builder
    private static class PatternInfo {
        private String pattern;
        private int flag;
    }

    @SuppressWarnings("MagicConstant")
    private static class PatternCacheLoader extends CacheLoader<PatternInfo, Pattern> {

        @Override
        public Pattern load(PatternInfo patternInfo) {
            return Pattern.compile(patternInfo.getPattern(), patternInfo.getFlag());
        }
    }
}
