package com.iwuyc.tools.commons.classtools.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@MyAnnotation
@Retention(RetentionPolicy.RUNTIME)
public @interface MyAnnotationChild {
}