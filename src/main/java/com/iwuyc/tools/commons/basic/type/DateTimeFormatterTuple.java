package com.iwuyc.tools.commons.basic.type;

import com.iwuyc.tools.commons.util.Conditionals;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

/**
 * 日期时间格式化的元组
 *
 * @author Neil
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DateTimeFormatterTuple implements Serializable {

    private static final long serialVersionUID = 1474397273416790482L;
    private final String pattern;
    private final Locale locale;
    private final Date baseTime;

    private DateTimeFormatterTuple(String pattern, Locale locale, Date baseTime){
        this.pattern = Conditionals.notNull(pattern);
        this.locale = Conditionals.notNull(locale);
        this.baseTime = baseTime;
    }

    public static DateTimeFormatterTuple create(String pattern, Locale locale, Date baseTime){
        return new DateTimeFormatterTuple(pattern, locale, baseTime);
    }

    public static DateTimeFormatterTuple create(String pattern, Locale locale){
        return create(pattern, locale, new Date());
    }
}
