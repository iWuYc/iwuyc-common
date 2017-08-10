/**
 * @Auth iWuYc
 * @since
 * @time 2017-08-07 16:25
 */
package com.iwuyc.tools.commons.classtools;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
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
    private static final Logger LOG = LoggerFactory.getLogger(ClassUtils.class);

    public static Optional<Class<?>> loadClass(String classPath)
    {
        return loadClass(classPath, null);
    }

    public static Optional<Class<?>> loadClass(String classPath, ClassLoader loader)
    {
        if (null == loader)
        {
            loader = ClassUtils.class.getClassLoader();
        }
        Class<?> result = null;
        try
        {
            result = loader.loadClass(classPath);
        }
        catch (ClassNotFoundException e)
        {

        }
        return Optional.ofNullable(result);
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
            if (!field.isAccessible())
            {
                field.setAccessible(true);
            }
            field.set(instance, rejectVal);
            return true;
        }
        catch (IllegalArgumentException | IllegalAccessException e)
        {
            LOG.error("Can't inject the field[{}] val[{}].cause:{}", field, val, e);
            return false;
        }
    }

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
        Optional<TypeConverter<? extends Object, ? extends Object>> supportConverterOpt = converters.stream()
                .filter((item) ->
                {
                    return item.support(targetType);
                }).findFirst();
        if (!supportConverterOpt.isPresent())
        {
            LOG.warn("Can't find any convert for this type[{}]", targetType);
            return null;
        }
        rejectVal = ((TypeConverter<Object, Object>) supportConverterOpt.get()).convert(val);
        return rejectVal;
    }

    public static <I> I instance(Class<I> targetClass, String clazzName, Object... args)
    {
        return AccessController.doPrivileged(new InstancePrivilegedAction<I>(targetClass, clazzName, args));
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
                Class<?> clazz = Class.forName(clazzName);
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
}
