package com.iwuyc.tools.commons.math;

import com.iwuyc.tools.commons.basic.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 数值范围
 *
 * @author @Neil
 * @since @2017年10月15日
 */
public class Range {
    private static class BoundaryNumber extends BigDecimal {

        private static final long serialVersionUID = 2910914454149571890L;
        private boolean isMax;
        private String name;

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
            return "BoundaryNumber [name=" + name + "]";
        }

        @Override
        public boolean equals(Object x) {
            return super.equals(x);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
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

        private boolean judg(BigDecimal number) {
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

    /**
     * 01
     *
     * @author @Neil
     */
    private static final byte CONTAIN_RIGHT = 1;
    /**
     * 10
     *
     * @author @Neil
     */
    private static final byte CONTAIN_LEAF = 2;

    private static final BigDecimal MAX = new BoundaryNumber("max", true);
    private static final BigDecimal MIN = new BoundaryNumber("min", false);

    private Collection<RangeItem> ranges = new ArrayList<>();

    /**
     * 编译表达式。表达式以区间表示，多个区间以"|"隔开，最大值以max表示，最小值以min表示
     *
     * @param rangeStr 表达式字符串。
     * @return range 实例
     * @throws IllegalArgumentException 如果表达式有问题，则会抛出这个错误。
     */
    public static Range compiler(String rangeStr) throws IllegalArgumentException {
        Range rootRange = new Range();
        String[] rangeStrArr = rangeStr.split("[|]+");
        RangeItem rangeItem = null;
        for (String rangeStrItem : rangeStrArr) {
            rangeStrItem = rangeStrItem.trim();
            if (StringUtils.isEmpty(rangeStrItem)) {
                continue;
            }
            rangeItem = itemCompiler(rangeStrItem);
            if (!rangeItem.verify()) {
                throw new IllegalArgumentException("The expression was wrong.Expression:" + rangeStrItem);
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

    private final static String MAX_TAG = "max";
    private final static String MIN_TAG = "min";

    private static BigDecimal builderBigDecimal(String numStr) {
        if (MAX_TAG.equals(numStr)) {
            return MAX;
        } else if (MIN_TAG.equals(numStr)) {
            return MIN;
        }
        return new BigDecimal(numStr);
    }

    private Range() {
    }

    /**
     * 判断一个数字是否在范围内。
     *
     * @param num 待判断的数字
     * @return 如果在范围内，则返回true，否则返回false。
     */
    public boolean inRange(Number num) {
        BigDecimal number = new BigDecimal(String.valueOf(num));
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
            if (range.judg(number)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Range [ranges=" + ranges + "]";
    }
}
