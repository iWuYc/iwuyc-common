package com.iwuyc.tools.commons.basic.type;

import lombok.Data;

import java.time.temporal.TemporalUnit;

/**
 * 日期时间元组
 *
 * @author Neil
 */
@Data
public class DateTimeTuple {
    private final long time;
    private final TemporalUnit temporalUnit;

    private DateTimeTuple(long time, TemporalUnit temporalUnit) {
        this.time = time;
        this.temporalUnit = temporalUnit;
    }

    public static DateTimeTuple create(long num, TemporalUnit temporalUnit) {
        return new DateTimeTuple(num, temporalUnit);
    }

}
