package com.iwuyc.tools.commons.math;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.iwuyc.tools.commons.exception.ExpressionException;
import com.iwuyc.tools.commons.util.string.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 数值范围
 *
 * <pre>
 * Example:
 *    · [0,10)|[10,100):
 *            0:include
 *           -1:exclude
 *           10:include
 *          100:exclude
 *      相当于[0,100)
 *
 *    · [0,10)|(10,100):
 *            0:include
 *           -1:exclude
 *           10:exclude
 *          100:exclude
 *
 *    · [0,10)|(9,100):
 *      相当于[0,100)
 * </pre>
 *
 * @author @Neil
 * @since @2017年10月15日
 */
public class Range {

    private static final Cache<String, Range> PATTERN_CACHE = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).maximumSize(1_000).build();
    /**
     * 01 右闭，表示含右侧的数字
     */
    private static final byte CONTAIN_RIGHT = 1;
    /**
     * 10 左闭，表示含左侧的数字
     */
    private static final byte CONTAIN_LEAF = 2;
    private static final BigDecimal MAX = new BoundaryNumber("max", true);
    private static final BigDecimal MIN = new BoundaryNumber("min", false);
    private static final String MAX_TAG = "max";
    private static final String MIN_TAG = "min";
    private final Collection<RangeItem> ranges = new ArrayList<>();

    private Range() {
    }

    /**
     * 编译表达式。表达式以区间表示，多个区间以"|"隔开，无限大以max表示，无限小以min表示。
     * <pre>
     * example:
     * [0,10]:表示0到10的数字，包含0跟10；
     * (0,10]:表示0到10的数字，不包含0，但包含10；
     * [0,10)|(10,20):表示0到20，但不包含10；
     * [min,10]:表示小于等于10的数值；
     * [0,max):表示大于等于0的数值；
     * </pre>
     * 对于min和max，是开还是闭已无所谓了，也就是说(min,max)等效于[min,max]
     *
     * @param rangeStr 表达式字符串。
     * @return range 实例
     * @throws IllegalArgumentException 如果表达式有问题，则会抛出这个错误。
     */
    public static Range compiler(String rangeStr) throws IllegalArgumentException {
        return compiler(rangeStr, true);
    }

    /**
     * 编译表达式。表达式以区间表示，多个区间以"|"隔开，最大值以max表示，最小值以min表示
     *
     * @param rangeStr 表达式字符串。
     * @return range 实例
     * @throws ExpressionException 如果表达式有问题，则会抛出这个错误。
     */
    public static Range compiler(String rangeStr, boolean cached) throws ExpressionException {

        try {
            final Callable<Range> rangeLoader = () -> getRange(rangeStr);
            if (cached) {
                return PATTERN_CACHE.get(rangeStr, rangeLoader);
            } else {
                return getRange(rangeStr);
            }
        } catch (Exception e) {
            final Throwable cause = e.getCause();
            if (cause instanceof ExpressionException) {
                throw (ExpressionException) cause;
            }
            throw new ExpressionException(e.getMessage(), e);
        }

    }

    private static Range getRange(String rangeStr) {
        Range rootRange = new Range();
        String[] rangeStrArr = rangeStr.split("[|]+");
        RangeItem rangeItem;
        for (String rangeStrItem : rangeStrArr) {
            rangeStrItem = rangeStrItem.trim();
            if (StringUtils.isEmpty(rangeStrItem)) {
                continue;
            }
            rangeItem = itemCompiler(rangeStrItem);
            if (!rangeItem.verify()) {
                throw new ExpressionException("The expression was wrong.Expression:" + rangeStrItem);
            }

            rootRange.ranges.add(rangeItem);

        }
        return rootRange;
    }

    private static RangeItem itemCompiler(String rangeStr) {
        RangeItem range = new RangeItem();

        StringBuilder sb = new StringBuilder(rangeStr);

        int firstStartIndex = sb.indexOf("[");
        if (firstStartIndex < 0) {
            firstStartIndex = sb.indexOf("(");
        } else {
            range.flag |= Range.CONTAIN_LEAF;
        }

        int splitFlagIndex = sb.indexOf(",");

        int secondEndIndex = sb.indexOf("]");
        if (secondEndIndex < 0) {
            secondEndIndex = sb.indexOf(")");
        } else {
            range.flag |= Range.CONTAIN_RIGHT;
        }

        String firstNumStr = sb.substring(firstStartIndex + 1, splitFlagIndex).trim();
        String secondNumStr = sb.substring(splitFlagIndex + 1, secondEndIndex).trim();

        range.min = builderBigDecimal(firstNumStr);
        range.max = builderBigDecimal(secondNumStr);
        return range;
    }

    private static BigDecimal builderBigDecimal(String numStr) {
        if (MAX_TAG.equals(numStr)) {
            return MAX;
        } else if (MIN_TAG.equals(numStr)) {
            return MIN;
        }
        return new BigDecimal(numStr);
    }

    /**
     * 判断一个数字是否在范围内。
     *
     * @param num 待判断的数字
     * @return 如果在范围内，则返回true，否则返回false。
     */
    public boolean inRange(Number num) {
        return inRange(String.valueOf(num));
    }

    /**
     * 判断一个数字是否在范围内。
     *
     * @param numStr 待判断的数字
     * @return 如果在范围内，则返回true，否则返回false。
     */
    public boolean inRange(String numStr) {
        BigDecimal number = new BigDecimal(numStr);
        return this.inRange(number);
    }

    /**
     * 判断一个数字是否在范围内。
     *
     * @param number 待判断的数字
     * @return 如果在范围内，则返回true，否则返回false。
     */
    public boolean inRange(BigDecimal number) {
        for (RangeItem range : ranges) {
            if (range.judge(number)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Range [ranges=" + ranges + "]";
    }

    private static class BoundaryNumber extends BigDecimal {

        private static final long serialVersionUID = 2910914454149571890L;
        private final boolean isMax;
        private final String name;

        public BoundaryNumber(String val, boolean isMax) {
            super("0");
            this.name = val;
            this.isMax = isMax;
        }

        @Override
        public int compareTo(BigDecimal val) {
            if (isMax) {
                return 1;
            }
            return -1;
        }

        @Override
        public String toString() {
            return "BoundaryNumber(" + name + ")";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            BoundaryNumber that = (BoundaryNumber) o;
            return isMax == that.isMax;
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (isMax ? 1 : 0);
            return result;
        }
    }

    private static class RangeItem {

        private BigDecimal min;
        private BigDecimal max;

        private byte flag = 0;

        @Override
        public String toString() {
            return "Range [min=" + min + ", max=" + max + ", flag=" + flag + "]";
        }

        private boolean judge(BigDecimal number) {
            int compareMin = this.min.compareTo(number);
            if (compareMin > 0) {
                return false;
            } else if (0 == compareMin) {
                return 0 != (this.flag & CONTAIN_LEAF);
            }
            int compareMax = this.max.compareTo(number);
            if (compareMax < 0) {
                return false;
            } else if (0 == compareMax) {
                return 0 != (this.flag & CONTAIN_RIGHT);
            }
            return true;
        }

        private boolean verify() {
            return this.max.compareTo(this.min) >= 0;
        }
    }
}
