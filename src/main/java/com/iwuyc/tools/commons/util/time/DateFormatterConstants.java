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
    String DEFAULT = "yyyy-MM-dd'T'HH:mm:ssZ";
    Locale DEFAULT_LOCALE = Locale.getDefault();
    ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();
    ZoneOffset DEFAULT_ZONE_OFFSET = DEFAULT_ZONE_ID.getRules().getOffset(Instant.now());
}
