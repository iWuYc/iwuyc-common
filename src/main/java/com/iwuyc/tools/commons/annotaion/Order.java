package com.iwuyc.tools.commons.annotaion;

import java.lang.annotation.*;

/**
 * 用于spi服务的排序，优先级，value的数值越大，优先级越高。
 * @author Neil
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Order {
    /**
     * @return 排序的权值，值越大，优先级越高。
     */
    int value() default Integer.MIN_VALUE;
}
