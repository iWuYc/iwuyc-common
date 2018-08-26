package com.iwuyc.tools.commons.basic.type;

import lombok.Data;

import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;

/**
 * @author @Neil
 * @since @2017年10月15日
 */
@Data
public class TimeTuple {
    private final long time;
    private final TimeUnit timeUnit;
    private final TemporalUnit temporalUnit;

    private TimeTuple(long time, TimeUnit timeUnit, TemporalUnit temporalUnit) {
        this.time = time;
        this.timeUnit = timeUnit;
        this.temporalUnit = temporalUnit;
    }

    public static TimeTuple create(long num, TimeUnit timeUnit) {
        return new TimeTuple(num, timeUnit, null);
    }

    public static TimeTuple create(long num, TemporalUnit temporalUnit) {
        return new TimeTuple(num, null, temporalUnit);
    }

}