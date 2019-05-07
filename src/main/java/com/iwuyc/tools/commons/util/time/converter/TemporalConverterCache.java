package com.iwuyc.tools.commons.util.time.converter;

import com.iwuyc.tools.commons.util.NumberUtils;
import com.iwuyc.tools.commons.util.time.SmartDateTimeFormatter;
import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TemporalConverterCache {
    private static final Map<Integer, TemporalConverter> TEMPORAL_CONVERTER_CACHE = new HashMap<>();

    static {
        TEMPORAL_CONVERTER_CACHE.put(SmartDateTimeFormatter.YEAR_MOD, Year::parse);
        TEMPORAL_CONVERTER_CACHE.put(SmartDateTimeFormatter.MONTH_MOD, new MonthConverter());
        TEMPORAL_CONVERTER_CACHE.put(SmartDateTimeFormatter.DAY_MOD, new DayConverter());
        TEMPORAL_CONVERTER_CACHE.put(SmartDateTimeFormatter.HOURS_MOD, new HourConverter());
        TEMPORAL_CONVERTER_CACHE.put(SmartDateTimeFormatter.MINUTES_MOD, new MinutesConverter());
        TEMPORAL_CONVERTER_CACHE.put(SmartDateTimeFormatter.SECONDS_MOD, new SecondsConverter());
        TEMPORAL_CONVERTER_CACHE.put(SmartDateTimeFormatter.MILLISECONDS_MOD, new MillisecondsConverter());
        TEMPORAL_CONVERTER_CACHE.put(SmartDateTimeFormatter.ZONE_DATE_MOD, ZonedDateTime::parse);
        // multy
        int modFlag = SmartDateTimeFormatter.YEAR_MOD | SmartDateTimeFormatter.MONTH_MOD;
        TEMPORAL_CONVERTER_CACHE.put(modFlag, YearMonth::parse);

        modFlag |= SmartDateTimeFormatter.DAY_MOD;
        TEMPORAL_CONVERTER_CACHE.put(modFlag, LocalDate::parse);

        modFlag = 0;
        modFlag = SmartDateTimeFormatter.HOURS_MOD | SmartDateTimeFormatter.MINUTES_MOD;
        TEMPORAL_CONVERTER_CACHE.put(modFlag, LocalTime::parse);

        modFlag |= SmartDateTimeFormatter.SECONDS_MOD;
        TEMPORAL_CONVERTER_CACHE.put(modFlag, LocalTime::parse);

        modFlag |= SmartDateTimeFormatter.MILLISECONDS_MOD;
        TEMPORAL_CONVERTER_CACHE.put(modFlag, LocalTime::parse);

    }

    public static TemporalConverter getTemporalConverter(int modFlag) {
        return TEMPORAL_CONVERTER_CACHE.get(modFlag);
    }

    public interface TemporalConverter {
        Temporal parse(CharSequence text, DateTimeFormatter formatter);
    }

    public static abstract class AbsTemporalConverter implements TemporalConverter {
        @Override
        public Temporal parse(CharSequence text, DateTimeFormatter formatter) {
            String val = String.valueOf(text);
            if (!NumberUtils.isInteger(val)) {
                throw new IllegalArgumentException("Wrong input[" + text + "].Range[1,12]");
            }
            int monthIndex = NumberUtils.parse(val, int.class);
            return innerParse(monthIndex);
        }

        public abstract Temporal innerParse(int val);
    }

    public static class MonthConverter extends AbsTemporalConverter {
        @Override
        public Temporal innerParse(int val) {
            return YearMonth.now().withMonth(val);
        }
    }

    public static class DayConverter extends AbsTemporalConverter {
        @Override
        public Temporal innerParse(int val) {
            return LocalDate.now().withDayOfMonth(val);
        }
    }

    public static class HourConverter extends AbsTemporalConverter {
        @Override
        public Temporal innerParse(int val) {
            return LocalTime.now().withHour(val);
        }
    }

    public static class MinutesConverter extends AbsTemporalConverter {
        @Override
        public Temporal innerParse(int val) {
            return LocalTime.now().withMinute(val);
        }
    }

    public static class SecondsConverter extends AbsTemporalConverter {
        @Override
        public Temporal innerParse(int val) {
            return LocalTime.now().withSecond(val);
        }
    }

    public static class MillisecondsConverter extends AbsTemporalConverter {
        @Override
        public Temporal innerParse(int val) {
            long nanoSeconds = TimeUnit.MILLISECONDS.convert(val, TimeUnit.NANOSECONDS);
            return LocalTime.now().withNano((int)nanoSeconds);
        }
    }
}
