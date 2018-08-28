package com.iwuyc.tools.commons.basic.type;

import com.iwuyc.tools.commons.util.Conditionals;
import lombok.Data;

import java.time.temporal.TemporalUnit;

/**
 * 日期时间元组
 *
 * @author Neil
 */
@Data
public class DateTimeTuple {
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

}
