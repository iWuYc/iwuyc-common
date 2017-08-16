package com.iwuyc.tools.commons.classtools;

import java.util.Collection;

import org.junit.Test;

import com.iwuyc.tools.commons.classtools.annotation.MyAnnotation;

public class AnnotationScannerTest
{

    @Test
    public void test() throws Exception
    {
        AnnotationScanner scanner = new AnnotationScanner(MyAnnotation.class,
                "com.iwuyc.tools.commons.classtools.annotation.classes");
        scanner.run();
        Collection<Class<?>> result = scanner.getResult();
        System.out.println(result);
    }

    @Test
    public void test1() throws Exception
    {
        AnnotationScanner scanner = new AnnotationScanner(MyAnnotation.class,
                "com.iwuyc.tools.commons.classtools.annotation");
        new Thread(scanner, "scanner").start();
        // LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(60));
        Collection<Class<?>> result = scanner.getResult();
        System.out.println(result);
    }

    @Test
    public void test2() throws Exception
    {
        AnnotationScanner scanner = new AnnotationScanner(MyAnnotation.class,
                "com.iwuyc.tools.commons");
        scanner.run();
        Collection<Class<?>> result = scanner.getResult();
        System.out.println(result);
    }
}
