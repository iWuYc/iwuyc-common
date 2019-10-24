package com.iwuyc.tools.commons.math;

import lombok.extern.slf4j.Slf4j;

/**
 * 数学工具类
 *
 * @author Neil
 */
@Slf4j
public class MathUtils {

    private static final char[] NUM_CHINESE = {'零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖'};
    private static final char[] UNITS = {' ', '拾', '佰', '仟'};
    private static final char[] BIG_UNITS = {'万', '亿', '万', '兆'};

    /**
     * 将数字转换为繁体文字
     *
     * @param num 待转换的数字
     * @return 返回转换后的结果
     */
    public static String numberTranslation(long num) {
        log.debug("Number:{}", num);
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
            } else if (unitsCount != 0) {
                sb.append(BIG_UNITS[unitsCount / 4 - 1]);
            }
            int temp = (int) (num % 10);
            if (temp == 0) {
                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                if (sb.length() > 0) {
                    if (sb.charAt(sb.length() - 1) != NUM_CHINESE[0]) {
                        sb.append(NUM_CHINESE[temp]);
                    }
                } else {
                    sb.append(NUM_CHINESE[temp]);
                }
            } else {
                sb.append(NUM_CHINESE[temp]);
            }
            num /= 10;
            unitsCount++;
        } while (num != 0);

        if (minus) {
            sb.append('负');
        }
        while (sb.length() > 1 && sb.charAt(0) == NUM_CHINESE[0]) {
            sb.deleteCharAt(0);
        }
        String result = sb.reverse().toString();
        log.debug("Result:{}", result);
        return result;
    }
}
