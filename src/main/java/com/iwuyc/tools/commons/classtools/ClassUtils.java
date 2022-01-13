package com.iwuyc.tools.commons.classtools;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.iwuyc.tools.commons.classtools.typeconverter.TypeConverter;
import com.iwuyc.tools.commons.classtools.typeconverter.TypeConverterConstant;
import com.iwuyc.tools.commons.util.collection.ArrayUtil;
import com.iwuyc.tools.commons.util.collection.CollectionUtil;
import com.iwuyc.tools.commons.util.collection.MultiMap;
import lombok.extern.slf4j.Slf4j;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.*;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;

/**
 * 类对象的工具类。
 *
 * @author Neil
 * @date 2017-08-07 16:25
 * @since JDK8
 */
@Slf4j
public abstract class ClassUtils {

    /**
     * 基础类型 跟 包装类型 的映射关系。
     */
    public final static Map<Class<?>, Class<?>> PRIMITIVE_TYPES_MAPPING_WRAPPED_TYPES;

    private static final Cache<TypeFunction<?, ?>, String> CACHE_LAMBDA_INFO = CacheBuilder.newBuilder().build();

    static {

        Map<Class<?>, Class<?>> temp = new HashMap<>();
        temp.put(void.class, Void.class);

        temp.put(byte.class, Byte.class);
        temp.put(short.class, Short.class);
        temp.put(int.class, Integer.class);
        temp.put(long.class, Long.class);

        temp.put(float.class, Float.class);
        temp.put(double.class, Double.class);

        temp.put(boolean.class, Boolean.class);
        temp.put(char.class, Character.class);

        PRIMITIVE_TYPES_MAPPING_WRAPPED_TYPES = Collections.unmodifiableMap(temp);
    }

    /**
     * 获取class类对象，不做类的初始化。以屏蔽讨厌的try……catch块。
     *
     * @param classPath 类的名字
     * @return 一个 {@link Optional} 对象，如果成功加载，则返回相应的对象，否则返回一个 {@link Optional#empty()}
     */
    public static Optional<Class<?>> loadClass(String classPath) {
        return loadClass(classPath, null);
    }

    public static Optional<Class<?>> loadClass(String classPath, ClassLoader loader) {
        return loadClass(classPath, false, loader);
    }

    public static Optional<Class<?>> loadClass(String classPath, boolean isInitialize, ClassLoader loader) {
        if (null == loader) {
            loader = ClassUtils.class.getClassLoader();
        }
        return AccessController.doPrivileged(new ClassLoadPrivilegedAction(classPath, isInitialize, loader));
    }

    /**
     * 将数据转换成对应的类型。
     *
     * @param sourceType     数据源类型
     * @param targetType     目标数据类型
     * @param val            数据
     * @param typeConverters 类型转换器集合
     * @return 转换结果
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Object convert(Class<?> sourceType, Class targetType, Object val,
                                  MultiMap<Class<?>, TypeConverter<?, ?>> typeConverters) {
        if (sourceType == targetType || compareType(sourceType, targetType, false)) {
            return val;
        }

        if (null == typeConverters) {
            typeConverters = TypeConverterConstant.DEFAULT_CONVERTERS;
        }

        Object rejectVal;
        Collection<TypeConverter<?, ?>> converters = typeConverters.get(sourceType);
        if (CollectionUtil.isEmpty(converters)) {
            log.warn("未找到对应的转换器，直接返回原值：sourceType->targetType:[{} -> {}];val:[{}];typeConverters:[{}]", sourceType, targetType, val, typeConverters);
            return val;
        }
        // 筛选支持转换的转换器，并且返回第一个。
        Optional<TypeConverter<?, ?>> supportConverterOpt = converters.stream()
                .filter((item) -> item.support(targetType)).findFirst();
        // 如果没有找到转换器，则直接将源数据返回。
        if (!supportConverterOpt.isPresent()) {
            log.warn("Can't find any convert for this type[{}]", targetType);
            return val;
        }
        rejectVal = ((TypeConverter<Object, Object>) supportConverterOpt.get()).convert(val, targetType);
        return rejectVal;
    }

    /**
     * 根据类名实例化一个对象。
     *
     * @param targetClass 返回的目标类型。
     * @param clazzName   类名。
     * @param args        构造函数的参数。
     * @return 实例化后的对象。
     */
    public static <I> I instance(Class<I> targetClass, String clazzName, Object... args) {
        Optional<Class<?>> clazzOpt = loadClass(clazzName);
        return clazzOpt.map(aClass -> instance(targetClass, aClass, args)).orElse(null);
    }

    /**
     * 根据类对象实例化一个对象
     *
     * @param targetClass 返回的目标类型
     * @param clazz       类对象
     * @param args        构造函数的参数
     * @return 实例化后的对象
     * @author Neil
     */
    public static <I> I instance(Class<I> targetClass, Class<?> clazz, Object... args) {
        return AccessController.doPrivileged(new InstancePrivilegedAction<>(targetClass, clazz, args));
    }

    /**
     * 根据类对象实例化一个对象
     *
     * @param clazz 类对象
     * @param args  构造函数的参数
     * @return 实例化后的对象
     * @author Neil
     */
    public static Object instance(Class<?> clazz, Object... args) {
        return instance(Object.class, clazz, args);
    }

    /**
     * 比较两个类型是否相同，主要是解决基础类型跟包装类型不一致的情况，如果不存在基础类型跟包装类型同时存在的比较，不建议使用该方法。
     *
     * @param firstType    第一个类型
     * @param another      第二个类型
     * @param isAssignable 是否依据继承关系进行判断。
     * @return 如果是同一种类型，则返回true，否则返回false;
     * @author Neil
     */
    public static boolean compareType(Class<?> firstType, Class<?> another, boolean isAssignable) {
        if (null == firstType || null == another) {
            return firstType == another;
        }

        if (firstType.isPrimitive()) {
            firstType = PRIMITIVE_TYPES_MAPPING_WRAPPED_TYPES.get(firstType);
            return firstType != null && firstType.equals(another);
        } else if (another.isPrimitive()) {
            another = PRIMITIVE_TYPES_MAPPING_WRAPPED_TYPES.get(another);
            return another != null && another.equals(firstType);
        }
        if (isAssignable) {
            return firstType.isAssignableFrom(another) || another.isAssignableFrom(firstType);
        }
        return firstType.equals(another);
    }

    /**
     * 比较两个类型的列表是否一致，包括基础类型跟包装类型。
     *
     * @param firstList    第一个类型列表
     * @param anotherList  另外一个类型列表
     * @param isAssignable 是否判断anotherList中的类型是否为firstList中的子类继承关系
     * @return 如果两个列表一致，则返回true，否则返回false；
     * @author Neil
     */
    public static boolean compareTypeList(Class<?>[] firstList, Class<?>[] anotherList, boolean isAssignable) {
        if (firstList.length != anotherList.length) {
            return false;
        }
        for (int i = 0; i < firstList.length; i++) {
            if (!compareType(firstList[i], anotherList[i], isAssignable)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 调用对象的方法，静态方法的话，请传入 Class 对象，而无需传入实例对象。
     *
     * @param instance   实例或者Class对象
     * @param methodName 方法名
     * @param parameters 方法的入参
     * @return 执行结果
     * @author Neil
     */
    public static Object callMethod(Object instance, String methodName, Object... parameters) {
        return callMethod0(instance, methodName, false, parameters);
    }

    /**
     * 忽略访问修饰符强制调用对象的方法，静态方法的话，请传入 Class 对象，而无需传入实例对象。
     *
     * @param instance   实例或者Class对象
     * @param methodName 方法名
     * @param parameters 方法的入参
     * @return 执行结果
     * @author Neil
     */
    public static Object mandatoryCallMethod(Object instance, String methodName, Object... parameters) {
        return callMethod0(instance, methodName, true, parameters);
    }

    private static Object callMethod0(Object instance, String methodName, boolean declared, Object... parameters) {

        Class<?> instanceType;
        if (instance instanceof Class) {
            instanceType = (Class<?>) instance;
        } else {
            instanceType = instance.getClass();
        }
        Class<?>[] parameterTypeList = typeList(parameters);
        MethodPrivilegedAction action = new MethodPrivilegedAction(instanceType, methodName, parameterTypeList,
                declared);
        Optional<Method> methodOpt = AccessController.doPrivileged(action);
        if (!methodOpt.isPresent()) {
            log.warn("Can't found any method for name:[{}];Parameter Type List:{}", methodName,
                    Arrays.toString(parameterTypeList));
            return null;
        }

        Method method = methodOpt.get();

        if (!method.isAccessible()) {
            method.setAccessible(true);
        }

        try {
            return method.invoke(instance, parameters);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            log.error("Call [{}] raise an error.Cause:[{}]", methodName, e.getMessage());
        }
        return null;

    }

    /**
     * 获取对象的类型列表，将列表中的对象的类型，按照输入的顺序进行输出。
     *
     * @param object 待获取的数据列表
     * @return 类型列表
     * @author Neil
     */
    public static Class<?>[] typeList(Object... object) {
        int parametersLength = ArrayUtil.arrayLength(object);

        Class<?>[] objectTypeList = new Class<?>[parametersLength];
        for (int i = 0; i < parametersLength; i++) {
            objectTypeList[i] = object[i].getClass();
        }
        return objectTypeList;
    }

    /**
     * 根据参数列表，从executable列表中获取最匹配的方法。
     *
     * @param executables    executable 列表
     * @param parameterTypes 参数类型列表
     * @return 参数列表最匹配的executable对象
     * @author Neil
     */
    public static Executable chooseBestMatchExecutable(Executable[] executables, Class<?>[] parameterTypes) {

        Integer[] constructorIndex = new Integer[executables.length];
        int cursor = 0;
        Executable constructor;
        for (int i = 0; i < executables.length; i++) {
            constructor = executables[i];
            if (compareTypeList(constructor.getParameterTypes(), parameterTypes, false)) {
                return constructor;
            } else if (compareTypeList(constructor.getParameterTypes(), parameterTypes, true)) {
                constructorIndex[cursor] = i;
                cursor++;
            }
        }

        Executable bestMatch = null;
        for (Integer index : constructorIndex) {
            if (null == index) {
                break;
            }
            if (null == bestMatch) {
                bestMatch = executables[index];
                continue;
            }
            if (compareTypeList(bestMatch.getParameterTypes(), executables[index].getParameterTypes(), true)) {
                bestMatch = executables[index];
            }
        }

        return bestMatch;

    }

    public static <T, R> String getLambdaMethodName(TypeFunction<T, R> function) {
        try {
            return CACHE_LAMBDA_INFO.get(function, () -> {
                Method writeReplaceMethod = null;
                for (Class<?> clazz = function.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
                    Method[] methods = clazz.getDeclaredMethods();
                    for (Method method : methods) {
                        if ("writeReplace".equals(method.getName())) {
                            writeReplaceMethod = method;
                            break;
                        }
                    }
                    if (writeReplaceMethod != null) {
                        break;
                    }
                }

                if (writeReplaceMethod == null) {
                    return "";
                }
                if (!writeReplaceMethod.isAccessible()) {
                    writeReplaceMethod.setAccessible(true);
                }
                Object replacement = writeReplaceMethod.invoke(function);
                if (!(replacement instanceof SerializedLambda)) {
                    return "";
                }
                SerializedLambda lambdaSerialized = (SerializedLambda) replacement;
                return lambdaSerialized.getImplMethodName();

            });
        } catch (Exception e) {
            // do nothing
            throw new IllegalStateException("获取方法名失败。", e);
        }
    }

    private static class InstancePrivilegedAction<I> implements PrivilegedAction<I> {

        private final Class<I> targetClass;
        private final Class<?> clazz;
        private final Object[] args;

        public InstancePrivilegedAction(Class<I> targetClass, Class<?> clazz, Object[] args) {
            this.targetClass = targetClass;
            this.clazz = clazz;
            this.args = args;
        }

        @SuppressWarnings("unchecked")
        @Override
        public I run() {
            try {
                if (!targetClass.isAssignableFrom(clazz)) {
                    return null;
                }
                Constructor<?> constructor = getConstructor(clazz);
                if (!constructor.isAccessible()) {
                    log.debug("The constructor can't visit.Set it true for accessible.");
                    constructor.setAccessible(true);
                }
                Object i = constructor.newInstance(args);
                return (I) i;
            } catch (Exception e) {
                log.debug("error:", e);
                log.error("Can't init class[{}]", clazz);
            }
            return null;
        }

        private Constructor<?> getConstructor(Class<?> clazz) throws NoSuchMethodException, SecurityException {
            if (ArrayUtil.isEmpty(args)) {
                return clazz.getDeclaredConstructor();
            }
            Class<?>[] parameterTypes = new Class<?>[args.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                parameterTypes[i] = args[i].getClass();
            }

            return (Constructor<?>) chooseBestMatchExecutable(clazz.getDeclaredConstructors(),
                    parameterTypes);
        }

    }

    private static class ClassLoadPrivilegedAction implements PrivilegedAction<Optional<Class<?>>> {

        private final ClassLoader loader;
        private final String classPath;
        private final boolean isInitialize;

        public ClassLoadPrivilegedAction(String classPath, boolean isInitialize, ClassLoader loader) {
            this.classPath = classPath;
            this.isInitialize = isInitialize;
            this.loader = loader;
        }

        @Override
        public Optional<Class<?>> run() {

            Class<?> result = null;
            try {
                result = Class.forName(this.classPath, this.isInitialize, this.loader);
            } catch (ClassNotFoundException e) {
                log.error("Can't found class:[{}]", classPath);
            }
            return Optional.ofNullable(result);
        }
    }

    private static class MethodPrivilegedAction implements PrivilegedAction<Optional<Method>> {

        private final Class<?> targetClazz;
        private final String methodName;
        private final Class<?>[] parameterTypeList;
        private final boolean declared;

        public MethodPrivilegedAction(Class<?> targetClazz, String methodName, Class<?>[] parameterTypeList,
                                      boolean declared) {
            this.targetClazz = targetClazz;
            this.methodName = methodName;
            this.parameterTypeList = parameterTypeList;
            this.declared = declared;
        }

        @Override
        public Optional<Method> run() {
            Method[] declaredMethods = declared ? targetClazz.getDeclaredMethods() : targetClazz.getMethods();

            Method[] nameMatchMethod = new Method[declaredMethods.length];
            int cursor = 0;
            for (Method method : declaredMethods) {
                if (!this.methodName.equals(method.getName())) {
                    continue;
                }
                nameMatchMethod[cursor] = method;
                cursor++;
            }
            nameMatchMethod = Arrays.copyOf(nameMatchMethod, cursor);
            Method bestMatchMethod = (Method) chooseBestMatchExecutable(nameMatchMethod, this.parameterTypeList);
            return Optional.ofNullable(bestMatchMethod);
        }
    }
}
