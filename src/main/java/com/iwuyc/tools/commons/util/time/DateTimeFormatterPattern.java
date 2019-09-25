package com.iwuyc.tools.commons.util.time;

/**
 * @author Neil
 */
public interface DateTimeFormatterPattern {

    String TIMEZONE_LONG_DATE_SECOND_FORMATTER = "yyyy-MM-dd'T'HH:mm:ssZ";
    String TIMEZONE_LONG_DATE_MILLISECOND_FORMATTER = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    String TIMEZONE_SHORT_DATE_YEAR_FORMATTER = "yyyy-MM-dd";
    String TIMEZONE_SHORT_DATE_YEAR_FORMATTER_WITHOUT_SPLIT = "yyyyMMdd";
}
