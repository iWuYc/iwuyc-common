package com.iwuyc.tools.commons.classtools;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwuyc.tools.commons.basic.StringUtils;

/**
 * 扫描包含指定注解的类，并返回这些类。
 * @Auth iWuYc
 * @since
 * @time 2017-08-04 15:23
 */
public class AnnotationScanner implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(AnnotationScanner.class);

    private final Stack<String> packages = new Stack<>();
    private final Collection<Class<?>> result;
    private final Class<? extends Annotation> annotation;

    public AnnotationScanner(Class<? extends Annotation> annotation, String... packages) {
        this.annotation = annotation;
        for (String packageName : packages) {
            if (StringUtils.isEmpty(packageName)) {
                continue;
            }
            this.packages.push(packageName);
        }
        this.result = new ArrayList<>();
    }

    @Override
    public void run() {
        try {
            String nextPackage = null;
            while (!packages.isEmpty()) {
                nextPackage = packages.pop();
                removeParentPackage(nextPackage);
                packageScanner(nextPackage);
            }
        }
        catch (Exception e) {
            LOG.warn("Raise an error when scanning package.", e);
        }
    }

    private void removeParentPackage(String nextPackage) {
        if (!packages.isEmpty()) {
            boolean isParent = nextPackage.startsWith(packages.peek());
            if (isParent) {
                packages.pop();
            }
        }
    }

    private void packageScanner(String packageName) throws Exception {
        String packageDirName = packageName.replace('.', '/');
        Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(packageDirName);

        URL url = null;
        String protocol = null;

        while (urls.hasMoreElements()) {
            url = urls.nextElement();
            protocol = url.getProtocol();
            switch (protocol) {
                case "file":
                    scannerAsDir(url, packageName);
                    break;
                case "jar":
                    scannerAsJar(url, packageName, packageDirName);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported protocol:" + protocol);
            }
        }
    }

    private void scannerAsJar(URL url, String packageName, String packageDirName) throws Exception {
        JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
        Enumeration<JarEntry> entries = jar.entries();

        JarEntry entry = null;
        String entriesName = null;
        String className = null;
        String classFullName = null;

        while (entries.hasMoreElements()) {
            entry = entries.nextElement();
            entriesName = entry.getName();
            if (entriesName.charAt(0) == '/') {
                entriesName = entriesName.substring(1);
            }

            if (!entriesName.startsWith(packageDirName)) {
                continue;
            }
            if (entry.isDirectory()) {
                reproducePackageAndPushStack(entriesName, packageDirName);
                continue;
            }
            else if (entriesName.endsWith(".class")) {
                className = extractClassName(entriesName);
                classFullName = toClassFullName(packageName, className);
                annotationClass(classFullName);
            }

        }
    }

    private String extractClassName(String entriesName) {
        String className = entriesName.substring(entriesName.lastIndexOf('/') + 1);
        return className;
    }

    /**
     * 根据JarEntry的名字重构包名，如果JarEntry的名字跟当前包路径名相同，则不放入栈中，防止重复扫描
     * @param entriesName JarEntry的名字
     * @param packageDirName 当前包名
     */
    private void reproducePackageAndPushStack(String entriesName, String packageDirName) {
        String newPackageName = entriesName.substring(0, entriesName.lastIndexOf('/'));
        if (packageDirName.equals(newPackageName)) {
            return;
        }
        this.packages.push(newPackageName);
    }

    private static final String CLASS_TEMPLATE = "%s.%s";

    private void scannerAsDir(URL url, String packageName) throws Exception {
        String dirPath = URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8.name());
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        String newPackage = null;
        String className = null;
        for (File file : files) {
            if (file.isDirectory()) {
                newPackage = packageName + '.' + file.getName();
                this.packages.push(newPackage);
                continue;
            }
            boolean isClassFile = file.getName().endsWith(".class");
            if (isClassFile) {
                className = toClassFullName(packageName, file.getName());
                annotationClass(className);
            }
        }
    }

    private void annotationClass(String className) throws ClassNotFoundException {
        int anonymityClassLocation = className.lastIndexOf('$');
        // 匿名内部类，则直接跳过
        if (anonymityClassLocation >= 0 && className.substring(anonymityClassLocation).matches("\\$[0-9]*")) {
            return;
        }
        Optional<Class<?>> clazzOpt = ClassUtils.loadClass(className, true, null);
        if (!clazzOpt.isPresent()) {
            return;
        }
        Class<?> clazz = clazzOpt.get();
        // 排除注解使用在annotation中的情况。
        if (Annotation.class.isAssignableFrom(clazz)) {
            return;
        }

        Annotation an = clazz.getAnnotation(this.annotation);
        // 当前类中没有该注解
        if (null == an) {
            // 尝试在注解中寻找该注解，如果不存在则直接返回
            if (!tryGetFromAnnotation(clazz)) {
                return;
            }
            // this.annotationStack.clear();
        }
        this.result.add(clazz);
    }

    private boolean tryGetFromAnnotation(Class<?> clazz) {
        // 用于防止重复扫描同一个annotation陷入死循环。
        Stack<Annotation> annotationStack = new Stack<>();
        Set<Annotation> scannerAlready = new HashSet<>();

        pushAnnotation2Stack(clazz, annotationStack);
        Annotation item = null;
        Annotation annotation = null;
        Class<?> nextScannerAnnotation = null;

        boolean result = false;
        while (!annotationStack.isEmpty()) {
            item = annotationStack.pop();
            if (scannerAlready.contains(item)) {
                break;
            }
            annotation = item.annotationType().getAnnotation(this.annotation);
            if (null != annotation) {
                result = true;
                break;
            }
            scannerAlready.add(item);

            nextScannerAnnotation = item.annotationType();
            pushAnnotation2Stack(nextScannerAnnotation, annotationStack);
        }
        annotationStack.clear();
        return result;
    }

    private void pushAnnotation2Stack(Class<?> clazz, Stack<Annotation> annotationStack) {
        Annotation[] annos = clazz.getAnnotations();
        for (Annotation annotation : annos) {
            annotationStack.push(annotation);
        }
    }

    private String toClassFullName(String packageName, String className) {
        className = className.substring(0, className.lastIndexOf('.'));
        return String.format(CLASS_TEMPLATE, packageName, className);
    }

    public Collection<Class<?>> getResult() {
        return result;
    }

}
