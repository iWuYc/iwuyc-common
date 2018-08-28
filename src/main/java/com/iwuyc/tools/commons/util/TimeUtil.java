package com.iwuyc.tools.commons.util;

import com.iwuyc.tools.commons.basic.AbstractStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.*;
import java.util.Date;

/**
 * 时间格式化工具类。由于使用了静态属性，因此一个项目只能有一个格式，如果需要有不同的格式，可以使用
 * {@link TimeUtil#createThreadSafeDateFormat(String)}创建线程安全的DateFormat对象。
 * 日期格式可以通过设置环境变量"timeformat"来定义，或者jvm的系统变量"timeformat"，如果都没有设置，则默认值为:"yyyy-MM-dd'T'HH:mm:ss.SSSZ"
 * @deprecated 弃用，使用DateTimeUtils进行数据格式转化
 * @author iWuYc
 */
@Deprecated
public class TimeUtil {
    private static final Logger LOG = LoggerFactory.getLogger(TimeUtil.class);

    private static final String FORMAT_PATTERN_KEY = "timeformat";

    private static final String DATE_FORMATER_PATTERN;
    private static final DateFormat THREADSAFE_DATE_FORMATTER;

    static {
        DATE_FORMATER_PATTERN = getDateFormat();
        THREADSAFE_DATE_FORMATTER = createThreadSafeDateFormat(DATE_FORMATER_PATTERN);
    }

    /**
     * 获取日期的格式
     * 
     * @author @iwuyc
     * @return 日期的格式
     */
    private static String getDateFormat() {
        String result = System.getProperty(FORMAT_PATTERN_KEY);
        result = AbstractStringUtils.isNotEmpty(result) ? result : System.getenv(FORMAT_PATTERN_KEY);
        result = AbstractStringUtils.isNotEmpty(result) ? result : "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
        return result;
    }

    /**
     * 创建{@link DateFormat}的{@link ThreadLocal}shi
     * 
     * @author @iwuyc
     * @param datePattern 日期格式
     * @return 线程本地变量
     */
    private static ThreadLocal<DateFormat> createDateFormatThreadLocal(final String datePattern) {
        ThreadLocal<DateFormat> threadSafe = ThreadLocal.withInitial(() -> new SimpleDateFormat(datePattern));
        return threadSafe;
    }

    /**
     * 将日期字符串转换为日期类型
     * 
     * @author @iwuyc
     * @param time
     *            时间字符串
     * @return 日期类型
     */
    public static Date parser(String time) {
        try {
            return THREADSAFE_DATE_FORMATTER.parse(time);
        }
        catch (ParseException e) {
            LOG.error("Format pattern:[{}].Can't parse the time:[{}].", DATE_FORMATER_PATTERN, time);
            return null;
        }
    }

    /**
     * 创建线程安全的 {@link DateFormat} 实例
     * 
     * @author @iwuyc
     * @param datePattern
     *            日期的格式
     * @return 日期格式化实例
     */
    private static DateFormat createThreadSafeDateFormat(String datePattern) {
        return new ThreadSafeDateFormat(datePattern);
    }

    /**
     * 格式化日期
     * 
     * @author @iwuyc
     * @param time
     *            格式化日期
     * @return 格式化后的日期字符串
     */
    public static String format(Date time) {
        return THREADSAFE_DATE_FORMATTER.format(time);
    }

    /**
     * @author @iwuyc
     */
    private static class ThreadSafeDateFormat extends DateFormat {

        private static final long serialVersionUID = -8321081491688772093L;

        private ThreadLocal<DateFormat> dateFormatFactory;

        ThreadSafeDateFormat(String datePattern) {
            this.dateFormatFactory = createDateFormatThreadLocal(datePattern);
        }

        @Override
        public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
            DateFormat simpleDateFormat = this.dateFormatFactory.get();
            return simpleDateFormat.format(date, toAppendTo, fieldPosition);
        }

        @Override
        public Date parse(String source, ParsePosition pos) {
            DateFormat simpleDateFormat = this.dateFormatFactory.get();
            return simpleDateFormat.parse(source, pos);
        }

        @Override
        protected void finalize() {
            this.dateFormatFactory.remove();
        }

        public void release() {
            dateFormatFactory.remove();
        }

    }
}
