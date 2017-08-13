/**
 * @Auth iWuYc
 * @since
 * @time 2017-08-07 16:25
 */
package com.iwuyc.tools.commons.classtools;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwuyc.tools.commons.basic.ArrayUtil;
import com.iwuyc.tools.commons.basic.MultiMap;
import com.iwuyc.tools.commons.classtools.typeconverter.TypeConverter;

/**
 * @Auth iWuYc
 * @since
 * @time 2017-08-07 16:25
 */
public abstract class ClassUtils
{

    private final static Field FIELD_MODIFIERS;
    static
    {
        FIELD_MODIFIERS = findField(Field.class, "modifiers");
    }

    private static class FieldPrivilegedAction implements PrivilegedAction<Field>
    {
        private Class<?> clazz;
        private String fieldName;

        public FieldPrivilegedAction(Class<?> clazz, String fieldName)
        {
            this.clazz = clazz;
            this.fieldName = fieldName;
        }

        @Override
        public Field run()
        {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields)
            {
                if (field.getName().equals(fieldName))
                {
                    return field;
                }
            }
            return null;
        }
    }

    private static class InstancePrivilegedAction<I> implements PrivilegedAction<I>
    {

        private Class<I> targetClass;
        private String clazzName;
        private Object[] args;

        public InstancePrivilegedAction(Class<I> targetClass, String clazzName, Object[] args)
        {
            this.targetClass = targetClass;
            this.clazzName = clazzName;
            this.args = args;
        }

        @SuppressWarnings("unchecked")
        @Override
        public I run()
        {
            try
            {
                Optional<Class<?>> clazzOpt = loadClass(clazzName);
                if (!clazzOpt.isPresent())
                {
                    return null;
                }

                Class<?> clazz = clazzOpt.get();

                if (!targetClass.isAssignableFrom(clazz))
                {
                    return null;
                }
                Constructor<?> constructor = getConstructor(clazz);

                Object i = constructor.newInstance(args);
                return (I) i;
            }
            catch (Exception e)
            {
                LOG.debug("error:{}", e);
                LOG.error("Can't init class[{}]", clazzName);
            }
            return null;
        }

        private Constructor<?> getConstructor(Class<?> clazz) throws NoSuchMethodException, SecurityException
        {
            if (ArrayUtil.isEmpty(args))
            {
                return clazz.getDeclaredConstructor();
            }
            Class<?>[] parameterTypes = new Class<?>[args.length];
            for (int i = 0; i < parameterTypes.length; i++)
            {
                parameterTypes[i] = args[i].getClass();
            }
            return clazz.getDeclaredConstructor(parameterTypes);
        }

    }

    private static class ClassLoadPrivilegedAction implements PrivilegedAction<Optional<Class<? extends Object>>>
    {

        private ClassLoader loader;
        private String classPath;
        private boolean isInitialize;

        public ClassLoadPrivilegedAction(String classPath, boolean isInitialize, ClassLoader loader)
        {
            this.classPath = classPath;
            this.isInitialize = isInitialize;
            this.loader = loader;
        }

        @Override
        public Optional<Class<? extends Object>> run()
        {

            Class<?> result = null;
            try
            {
                result = Class.forName(this.classPath, this.isInitialize, this.loader);
            }
            catch (ClassNotFoundException e)
            {
                LOG.error("Can't found class:[{}]", classPath);
            }
            return Optional.ofNullable(result);
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(ClassUtils.class);

    /**
     * 获取class类对象，不做类的初始化。以屏蔽讨厌的try……catch块。
     * 
     * @param classPath
     *            类的名字
     * @return 一个 {@link Optional} 对象，如果成功加载，则返回相应的对象，否则返回一个
     *         {@link Optional#empty()}
     */
    public static Optional<Class<?>> loadClass(String classPath)
    {
        return loadClass(classPath, null);
    }

    public static Optional<Class<?>> loadClass(String classPath, ClassLoader loader)
    {
        return loadClass(classPath, false, loader);
    }

    public static Optional<Class<?>> loadClass(String classPath, boolean isInitialize, ClassLoader loader)
    {
        if (null == loader)
        {
            loader = ClassUtils.class.getClassLoader();
        }
        return AccessController.doPrivileged(new ClassLoadPrivilegedAction(classPath, isInitialize, loader));
    }

    /**
     * 将map中的值，按field的名字注入到instance中。
     * 
     * @param instance
     *            实例
     * @param fieldAndVal
     *            字段跟值（这个map的键值应该是Map<String,Object>）
     * @param typeConverters
     *            类型转换器，用于注入前的类型转换。key是源类型，val是转换器列表。
     * @return 未注入成功的字段跟值，一般是，不存在这个字段，或者，在注入的时候出问题了
     */
    public static Map<Object, Object> injectFields(Object instance, Map<String, Object> fieldAndVal,
            MultiMap<Class<? extends Object>, TypeConverter<? extends Object, ? extends Object>> typeConverters)
    {
        if (null == instance || null == fieldAndVal)
        {
            return Collections.emptyMap();
        }

        HashMap<Object, Object> innerMap = new HashMap<>(fieldAndVal);

        Class<? extends Object> clazz = instance.getClass();
        Field[] fields = clazz.getDeclaredFields();
        String fieldName = null;
        Object fieldVal = null;
        for (Field field : fields)
        {
            fieldName = field.getName();
            fieldVal = innerMap.get(fieldName);
            if (null == fieldVal)
            {
                continue;
            }
            if (injectField(instance, field, fieldVal, typeConverters))
            {
                innerMap.remove(fieldName);
            }
        }

        return innerMap;
    }

    private static boolean injectField(Object instance, Field field, Object val,
            MultiMap<Class<? extends Object>, TypeConverter<? extends Object, ? extends Object>> typeConverters)
    {
        try
        {

            Object rejectVal = convert(val.getClass(), field.getType(), val, typeConverters);
            if (null == rejectVal)
            {
                return false;
            }

            // 字段属性修改，以便可以进行属性设置
            fieldModifier(field);
            return injectField(instance, field, rejectVal);
        }
        catch (IllegalArgumentException e)
        {
            LOG.error("Can't inject the field[{}] val[{}].cause:{}", field, val, e);
            return false;
        }
    }

    private static boolean injectField(Object instance, Field field, Object rejectVal)
    {
        try
        {
            field.set(instance, rejectVal);
            return true;
        }
        catch (IllegalArgumentException | IllegalAccessException e)
        {

            LOG.error("Can't inject the field[{}] val[{}].cause:{}", field, rejectVal, e);
            return false;
        }
    }

    /**
     * 对一些有访问限制的字段进行修改，以便可以正常访问进行数据修改。
     * 
     * @param field
     *            待修改字段。
     */
    private static void fieldModifier(Field field)
    {
        if (!field.isAccessible())
        {
            field.setAccessible(true);
        }
        int newModifies = field.getModifiers();
        if (Modifier.isFinal(newModifies))
        {
            newModifies = newModifies & ~Modifier.FINAL;
        }
        injectField(field, FIELD_MODIFIERS, newModifies);
    }

    /**
     * 将数据转换成对应的类型。
     * 
     * @param sourceType
     *            数据源类型
     * @param targetType
     *            目标数据类型
     * @param val
     *            数据
     * @param typeConverters
     *            类型转换器集合
     * @return
     */
    @SuppressWarnings("unchecked")
    private static Object convert(Class<? extends Object> sourceType, Class<?> targetType, Object val,
            MultiMap<Class<? extends Object>, TypeConverter<? extends Object, ? extends Object>> typeConverters)
    {
        if (sourceType == targetType)
        {
            return val;
        }

        Object rejectVal = null;
        Collection<TypeConverter<? extends Object, ? extends Object>> converters = typeConverters.get(sourceType);
        // 筛选支持转换的转换器，并且返回第一个。
        Optional<TypeConverter<? extends Object, ? extends Object>> supportConverterOpt = converters.stream()
                .filter((item) ->
                {
                    return item.support(targetType);
                }).findFirst();
        // 如果没有找到转换器，则直接将源数据返回。
        if (!supportConverterOpt.isPresent())
        {
            LOG.warn("Can't find any convert for this type[{}]", targetType);
            return val;
        }
        rejectVal = ((TypeConverter<Object, Object>) supportConverterOpt.get()).convert(val);
        return rejectVal;
    }

    /**
     * 根据类名实例化一个对象。
     * 
     * @param targetClass
     *            返回的目标类型。
     * @param clazzName
     *            类名。
     * @param args
     *            构造函数的参数。
     * @return 实例化后的对象。
     */
    public static <I> I instance(Class<I> targetClass, String clazzName, Object... args)
    {
        return AccessController.doPrivileged(new InstancePrivilegedAction<I>(targetClass, clazzName, args));
    }

    /**
     * 获取属性对象
     * 
     * @param clazz
     * @param fieldName
     * @return
     */
    public static Field findField(Class<?> clazz, String fieldName)
    {
        return AccessController.doPrivileged(new FieldPrivilegedAction(clazz, fieldName));
    }
}
