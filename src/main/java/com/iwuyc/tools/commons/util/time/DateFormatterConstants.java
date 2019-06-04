package com.iwuyc.tools.commons.util.time;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Locale;

/**
 * Date Formatter Pattern
 *
 * @author Neil
 */
public interface DateFormatterConstants {
    String DEFAULT_PATTERN = DateTimeFormatterPattern.TIMEZONE_LONG_DATE_SECOND_FORMATTER;
    Locale DEFAULT_LOCALE = Locale.getDefault();
    ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();
    ZoneOffset DEFAULT_ZONE_OFFSET = DEFAULT_ZONE_ID.getRules().getOffset(Instant.now());
}
