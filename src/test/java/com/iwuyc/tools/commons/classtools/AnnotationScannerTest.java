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

}
