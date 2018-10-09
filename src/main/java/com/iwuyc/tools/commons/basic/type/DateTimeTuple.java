package com.iwuyc.tools.commons.basic.type;

import com.iwuyc.tools.commons.util.Conditionals;
import lombok.Data;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

/**
 * 日期时间元组
 *
 * @author Neil
 */
@Data
public class DateTimeTuple {

    public static final DateTimeTuple CHRONO_ONE_NANOSECOND = create(1, ChronoUnit.NANOS);
    public static final DateTimeTuple CHRONO_ONE_MILLISECOND = create(1, ChronoUnit.MILLIS);
    public static final DateTimeTuple CHRONO_ONE_SECOND = create(1, ChronoUnit.SECONDS);
    public static final DateTimeTuple CHRONO_ONE_MINUTES = create(1, ChronoUnit.MINUTES);
    public static final DateTimeTuple CHRONO_ONE_HOURS = create(1, ChronoUnit.HOURS);
    public static final DateTimeTuple CHRONO_ONE_DAYS = create(1, ChronoUnit.DAYS);
    public static final DateTimeTuple CHRONO_ONE_MONTH = create(1, ChronoUnit.MONTHS);
    public static final DateTimeTuple CHRONO_ONE_YEARS = create(1, ChronoUnit.YEARS);

    /**
     * 时间长度
     */
    private final long time;
    /**
     * 时间的单位
     */
    private final TemporalUnit temporalUnit;

    private DateTimeTuple(long time, TemporalUnit temporalUnit) {
        this.time = time;
        this.temporalUnit = Conditionals.notNull(temporalUnit);
    }

    public static DateTimeTuple create(long num, TemporalUnit temporalUnit) {
        return new DateTimeTuple(num, temporalUnit);
    }

    public static DateTimeTuple create(long num, ChronoUnit chronoUnit) {
        return create(num, (TemporalUnit) chronoUnit);
    }

}
