package com.iwuyc.tools.commons.basic.type;

import com.iwuyc.tools.commons.util.Conditionals;
import lombok.Data;

import java.io.Serializable;
import java.util.Locale;

/**
 * 日期时间格式化的元组
 *
 * @author Neil
 */
@Data
public class DateTimeFormatterTuple implements Serializable {
    private static final long serialVersionUID = 1474397273416790482L;
    private final String pattern;
    private final Locale locale;

    private DateTimeFormatterTuple(String pattern, Locale locale) {
        this.pattern = Conditionals.notNull(pattern);
        this.locale = Conditionals.notNull(locale);
    }

    public static DateTimeFormatterTuple create(String pattern, Locale locale) {
        return new DateTimeFormatterTuple(pattern, locale);
    }
}
