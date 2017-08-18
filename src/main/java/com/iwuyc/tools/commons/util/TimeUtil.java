package com.iwuyc.tools.commons.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author iWuYc
 *
 */
public class TimeUtil
{
    private static final Logger LOG = LoggerFactory.getLogger(TimeUtil.class);

    private static final String DATE_FORMATER_PATTERN;
    private static final ThreadLocal<DateFormat> THREADLOCAL_DATE_FORMATTER;
    static
    {
        DATE_FORMATER_PATTERN = System.getProperty("timeformat", "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        THREADLOCAL_DATE_FORMATTER = createDateFormatThreadLocal(DATE_FORMATER_PATTERN);
    }

    public static ThreadLocal<DateFormat> createDateFormatThreadLocal(final String datePattern)
    {
        ThreadLocal<DateFormat> threadSafe = ThreadLocal.withInitial(() ->
        {
            return new SimpleDateFormat(datePattern);
        });
        return threadSafe;
    }

    public static Date parser(String time)
    {
        try
        {
            DateFormat format = THREADLOCAL_DATE_FORMATTER.get();
            return format.parse(time);
        }
        catch (ParseException e)
        {
            LOG.error("Format pattern:[{}].Can't parse the time:[{}].", DATE_FORMATER_PATTERN, time);
            return null;
        }
        finally
        {
            THREADLOCAL_DATE_FORMATTER.remove();
        }
    }

    public static String format(Date time)
    {
        try
        {
            DateFormat format = THREADLOCAL_DATE_FORMATTER.get();
            return format.format(time);
        }
        finally
        {
            THREADLOCAL_DATE_FORMATTER.remove();
        }
    }
}
