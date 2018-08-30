package com.iwuyc.tools.commons.util.time;

import java.time.ZoneOffset;
import java.util.Locale;

/**
 * Date Formatter Pattern
 *
 * @author Neil
 */
public interface DateFormatterPattern {
    String DEFAULT = "yyyy-MM-dd'T'HH:mm:ssZ";
    Locale DEFAULT_LOCALE = Locale.PRC;
    ZoneOffset DEFAULT_ZONE_OFFSET = ZoneOffset.ofHours(8);
}
