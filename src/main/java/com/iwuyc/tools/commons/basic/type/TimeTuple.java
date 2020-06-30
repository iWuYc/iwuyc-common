package com.iwuyc.tools.commons.basic.type;

import lombok.Data;

import java.util.concurrent.TimeUnit;

/**
 * @author @Neil
 * @since @2017年10月15日
 */
@Data
public class TimeTuple {
    private final long time;
    private final TimeUnit timeUnit;

    private TimeTuple(long time, TimeUnit timeUnit) {
        this.time = time;
        this.timeUnit = timeUnit;
    }

    public static TimeTuple create(long num, TimeUnit timeUnit) {
        return new TimeTuple(num, timeUnit);
    }

    public long toTime(TimeUnit timeUnit) {
        return timeUnit.convert(time, this.timeUnit);
    }

}