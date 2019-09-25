package com.iwuyc.tools.commons.util.string;

import com.iwuyc.tools.commons.math.Range;
import com.iwuyc.tools.commons.util.NumberUtils;

public class VerifyUtils {
    private static final Range IP_RANGE = Range.compiler("[0,255]");

    public static boolean isIpV4(String ip) {
        String[] values = ip.split("\\.");
        for (String item : values) {
            if (!NumberUtils.isInteger(item) || !IP_RANGE.inRange(NumberUtils.parse(item))) {
                return false;
            }
        }
        return true;
    }
}
