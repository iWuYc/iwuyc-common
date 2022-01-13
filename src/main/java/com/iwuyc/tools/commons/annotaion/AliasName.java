package com.iwuyc.tools.commons.annotaion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 别名，用于给服务取别名，方便配置引用
 *
 * @author Neil
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AliasName {
    /**
     * @return 别名
     */
    String value();
}
