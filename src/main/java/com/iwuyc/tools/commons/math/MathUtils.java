package com.iwuyc.tools.commons.math;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数学工具类
 *
 * @author Neil
 */
public class MathUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(MathUtils.class);

    private static final char[] NumChinese = {'零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖'};
    private static final char[] UNITS = {' ', '拾', '佰', '仟'};
    private static final String[] BIGUNITS = {"万", "亿", "亿万", "兆"};

    /**
     * 将数字转换为繁体文字
     *
     * @param num 待转换的数字
     * @return 返回转换后的结果
     */
    public static String numberTranslation(long num) {
        LOGGER.debug("Number:{}", num);
        StringBuilder sb = new StringBuilder();
        boolean minus = false;
        if (num < 0) {
            minus = true;
            num *= -1;
        }
        int unitsCount = 0;
        do {
            int unitsCountTemp = unitsCount % 4;
            if (unitsCountTemp != 0) {
                sb.append(UNITS[unitsCountTemp]);
            } else if (unitsCount != 0 && unitsCountTemp == 0) {
                sb.append(BIGUNITS[unitsCount / 4 - 1]);
            }
            int temp = (int)(num % 10);
            sb.append(NumChinese[temp]);
            num /= 10;
            unitsCount++;
        } while (num != 0);

        if (minus) {
            sb.append('负');
        }
        String result = sb.reverse().toString();
        LOGGER.debug("Result:{}", result);
        return result;
    }
}
