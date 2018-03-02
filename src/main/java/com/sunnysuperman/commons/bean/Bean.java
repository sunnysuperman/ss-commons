package com.sunnysuperman.commons.bean;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.sunnysuperman.commons.util.FormatUtil;
import com.sunnysuperman.commons.util.JSONUtil;

/**
 * Bean解析核心类
 *
 */
public class Bean {

    public static <T> T fromMap(Map<?, ?> map, T bean) {
        return fromMap(map, bean, null, null);
    }

    public static <T> T fromMap(Map<?, ?> map, T bean, ParseBeanOptions options) {
        return fromMap(map, bean, options, new LinkedList<String>());
    }

    private static <T> T fromMap(Map<?, ?> map, T bean, ParseBeanOptions options, LinkedList<String> contextKeys) {
        if (options == null) {
            options = new ParseBeanOptions();
        }
        if (options.isInjectContext()) {
            if (options.getInterceptor() != null) {
                if (contextKeys == null) {
                    contextKeys = new LinkedList<>();
                }
            } else {
                contextKeys = null;
            }
        } else {
            contextKeys = null;
        }
        Method[] methods = bean.getClass().getMethods();
        String methodName = null;
        String key = null;
        Object value = null;
        Map<String, Field> fields = BeanUtil.getAllFields(bean.getClass());
        int contextDepth = contextKeys != null ? contextKeys.size() : 0;
        for (Method method : methods) {
            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            Class<?>[] paramTypes = method.getParameterTypes();
            if (paramTypes.length != 1) {
                continue;
            }
            if (method.isAnnotationPresent(BeanIgnore.class)) {
                continue;
            }
            methodName = method.getName();
            if (!methodName.startsWith("set")) {
                continue;
            }
            key = getFieldName(methodName, 3);
            if (key == null) {
                continue;
            }
            value = map.get(key);
            if (value == null) {
                continue;
            }
            ParameterizedType pType = null;
            Field field = fields.get(key);
            if (field != null) {
                Type type = field.getGenericType();
                if (type instanceof ParameterizedType) {
                    pType = (ParameterizedType) type;
                }
            }
            Class<?> destClass = method.getParameterTypes()[0];
            if (contextKeys != null) {
                contextKeys.addLast(key);
            }
            value = parse(value, destClass, pType, options, contextKeys);
            if (contextKeys != null) {
                int removeSize = contextKeys.size() - contextDepth;
                for (int i = 0; i < removeSize; i++) {
                    contextKeys.removeLast();
                }
            }
            if (value == null) {
                continue;
            }
            try {
                method.invoke(bean, value);
            } catch (Exception e) {
                throw new BeanException(
                        "Failed to invoke " + method.getName() + " of " + method.getDeclaringClass().getName(), e);
            }
        }
        return bean;
    }

    public static <T> T fromJson(String s, T bean) {
        return fromJson(s, bean, null);
    }

    public static <T> T fromJson(String s, T bean, ParseBeanOptions options) {
        Map<String, Object> map = JSONUtil.parseJSONObject(s);
        if (map == null) {
            throw new BeanException("Failed to parse json string to bean: " + s);
        }
        return fromMap(map, bean, options, null);
    }

    public static <T> List<T> fromJson(String s, Class<T> clazz) {
        return fromJson(s, clazz, null);
    }

    public static <T> List<T> fromJson(String s, Class<T> clazz, ParseBeanOptions options) {
        List<?> items = JSONUtil.parseJSONArray(s);
        if (items == null) {
            return null;
        }
        List<T> beans = new ArrayList<T>(items.size());
        for (Object item : items) {
            Map<?, ?> jsonObject = (Map<?, ?>) item;
            T bean;
            try {
                bean = clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new BeanException("Failed to newInstance of " + clazz, e);
            }
            bean = fromMap(jsonObject, bean, options, null);
            beans.add(bean);
        }
        return beans;
    }

    public static Map<String, Object> toMap(Object bean) {
        Map<String, Object> map = new HashMap<String, Object>();
        Method[] methods = bean.getClass().getMethods();
        String methodName = null;
        String field = null;
        Object value = null;
        for (Method method : methods) {
            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            Class<?>[] paramTypes = method.getParameterTypes();
            if (paramTypes.length != 0) {
                continue;
            }
            if (method.isAnnotationPresent(BeanIgnore.class)) {
                continue;
            }
            methodName = method.getName();
            int offset = 0;
            if (methodName.startsWith("get")) {
                offset = 3;
            } else if (methodName.startsWith("is")) {
                offset = 2;
            } else {
                continue;
            }
            field = getFieldName(methodName, offset);
            if (field == null || field.equals("class")) {
                continue;
            }
            try {
                value = method.invoke(bean);
            } catch (Exception e) {
                throw new BeanException(
                        "Failed to invoke " + method.getName() + " of " + method.getDeclaringClass().getName(), e);
            }
            map.put(field, value);
        }
        return map;
    }

    public static Map<String, Object> toSerializedMap(Object bean) {
        Map<String, Object> map = JSONUtil.parseJSONObject(JSONUtil.toJSONString(bean));
        if (map == null) {
            throw new BeanException("Failed to call bean2serializedMap");
        }
        return map;
    }

    public static <T> T extend(T dest, Object src) {
        fromMap(toMap(src), dest, null, null);
        return dest;
    }

    // ////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 获取属性名
     * 
     * @param methodName
     *            方法名
     * @param offset
     *            偏移
     * @return 属性名，偏移量>=方法名长度，返回null，截取长度为1，则返回一个长度的小写字符串，否则，返回首字母小写的字符串
     */
    private static String getFieldName(String methodName, int offset) {
        if (methodName.length() <= offset) {
            return null;
        }
        String field = methodName.substring(offset);
        if (field.length() == 1) {
            return field.toLowerCase();
        }
        return field.toLowerCase().charAt(0) + field.substring(1);
    }

    /**
     * 解析json string
     * 
     * @param raw
     *            待解析的对象
     * @return 解析后的对象，若raw参数为string类型，则
     */
    private static Object parseIfIsJSONString(Object raw) {
        if (raw instanceof String) {
            String s = (String) raw;
            if (JSONUtil.isJSONString(s)) {
                return JSON.parse(s);
            }
        }
        return raw;
    }

    public static Object parse(Object raw, Class<?> destClass) {
        return parse(raw, destClass, null, null, null);
    }

    public static Object parse(Object raw, Class<?> destClass, ParameterizedType pType, ParseBeanOptions options,
            LinkedList<String> contextKeys) {
        if (raw == null) {
            return null;
        }
        if (raw.getClass().equals(destClass)) {
            return raw;
        }
        // most case
        if (destClass.equals(String.class)) {
            return raw.toString();
        }
        if (destClass.equals(Boolean.class)) {
            return FormatUtil.parseBooleanStrictly(raw);
        }
        if (destClass.equals(boolean.class)) {
            return FormatUtil.parseBoolean(raw, Boolean.FALSE);
        }
        if (destClass.equals(Integer.class)) {
            return FormatUtil.parseInteger(raw);
        }
        if (destClass.equals(int.class)) {
            Integer integer = FormatUtil.parseInteger(raw);
            return integer == null ? 0 : integer.intValue();
        }
        if (destClass.equals(Long.class)) {
            return FormatUtil.parseLong(raw);
        }
        if (destClass.equals(long.class)) {
            Long l = FormatUtil.parseLong(raw);
            return l == null ? 0L : l.longValue();
        }
        if (destClass.equals(Double.class)) {
            return FormatUtil.parseDouble(raw);
        }
        if (destClass.equals(double.class)) {
            Double d = FormatUtil.parseDouble(raw);
            return d == null ? 0d : d.doubleValue();
        }
        if (destClass.equals(Float.class)) {
            return FormatUtil.parseFloat(raw);
        }
        if (destClass.equals(float.class)) {
            Float f = FormatUtil.parseFloat(raw);
            return f == null ? 0f : f.floatValue();
        }
        if (destClass.equals(Date.class)) {
            return FormatUtil.parseDate(raw);
        }
        if (destClass.equals(Short.class)) {
            return FormatUtil.parseShort(raw);
        }
        if (destClass.equals(short.class)) {
            Short s = FormatUtil.parseShort(raw);
            return s == null ? (short) 0 : s.shortValue();
        }
        if (destClass.equals(Byte.class)) {
            return FormatUtil.parseByte(raw);
        }
        if (destClass.equals(byte.class)) {
            Byte s = FormatUtil.parseByte(raw);
            return s == null ? (byte) 0 : s.byteValue();
        }
        if (destClass.equals(Character.class) || destClass.equals(char.class)) {
            return raw.toString().charAt(0);
        }

        // Array
        if (destClass.isArray()) {
            raw = parseIfIsJSONString(raw);
            return parseArray(raw, destClass.getComponentType(), options, contextKeys);
        }

        // Map
        if (Map.class.isAssignableFrom(destClass)) {
            raw = parseIfIsJSONString(raw);
            Class<?> valueClass = Object.class;
            Type type = (Type) destClass;
            if (type instanceof ParameterizedType) {
                pType = (ParameterizedType) type;
            }
            if (pType != null) {
                Type valueType = pType.getActualTypeArguments()[1];
                if (valueType instanceof Class) {
                    valueClass = (Class<?>) valueType;
                }
            }
            @SuppressWarnings("unchecked")
            Map<?, Object> rawMap = (Map<?, Object>) raw;
            for (Entry<?, Object> entry : rawMap.entrySet()) {
                entry.setValue(parse(entry.getValue(), valueClass, null, options, contextKeys));
            }
            return rawMap;
        }

        // List/Set
        if (Collection.class.isAssignableFrom(destClass)) {
            raw = parseIfIsJSONString(raw);
            Class<?> componentClass = Object.class;
            Type type = (Type) destClass;
            if (type instanceof ParameterizedType) {
                pType = (ParameterizedType) type;
            }
            if (pType != null) {
                Type componentType = pType.getActualTypeArguments()[0];
                if (componentType instanceof Class) {
                    componentClass = (Class<?>) componentType;
                }
            }
            if (List.class.isAssignableFrom(destClass)) {
                return parseCollection(raw, componentClass, new ArrayList<Object>(), options, contextKeys);
            } else if (Set.class.isAssignableFrom(destClass)) {
                return parseCollection(raw, componentClass, new HashSet<Object>(), options, contextKeys);
            }
        }

        // raw bean
        if (destClass.equals(Object.class)) {
            return raw;
        }

        // if is bean
        raw = parseIfIsJSONString(raw);
        if (Map.class.isAssignableFrom(raw.getClass())) {
            ParseBeanResult result = null;
            if (options != null && options.getInterceptor() != null) {
                result = options.getInterceptor().parse(raw, destClass, pType, contextKeys);
            }
            if (result != null) {
                return result.getValue();
            } else {
                Object object;
                try {
                    object = destClass.newInstance();
                } catch (Exception e) {
                    throw new BeanException("Failed to create instance of " + destClass, e);
                }
                return fromMap((Map<?, ?>) raw, object, options, contextKeys);
            }
        }

        return raw;
    }

    /**
     * 解析对象集合
     * 
     * @param raw
     *            对象集合，数组或集合
     * @param componentType
     *            需解析成的class
     * @param newCollection
     *            解析后的新集合
     * @param options
     *            解析配置项
     * @return 解析后的新集合 @
     */
    private static Object parseCollection(Object raw, Class<?> componentType, Collection<Object> newCollection,
            ParseBeanOptions options, LinkedList<String> contextKeys) {
        if (raw.getClass().isArray()) {
            int length = Array.getLength(raw);
            for (int i = 0; i < length; i++) {
                Object item = Array.get(raw, i);
                Object parsedItem = parse(item, componentType, null, options, contextKeys);
                newCollection.add(parsedItem);
            }
            return newCollection;
        } else if (raw instanceof Collection) {
            Collection<?> collection = (Collection<?>) raw;
            for (Object item : collection) {
                Object parsedItem = parse(item, componentType, null, options, contextKeys);
                newCollection.add(parsedItem);
            }
            return newCollection;
        }
        throw new BeanException("Failed to parseCollection: " + raw.getClass());
    }

    /**
     * 解析对象集合
     * 
     * @param raw
     *            对象集合，数组或集合
     * @param componentType
     *            需解析成的class
     * @param options
     *            解析配置项
     * @return 解析后的新集合 @
     */
    private static Object parseArray(Object raw, Class<?> componentType, ParseBeanOptions options,
            LinkedList<String> contextKeys) {
        if (raw.getClass().isArray()) {
            int length = Array.getLength(raw);
            Object newArray = Array.newInstance(componentType, length);
            for (int i = 0; i < length; i++) {
                Object item = Array.get(raw, i);
                Object parsedItem = parse(item, componentType, null, options, contextKeys);
                Array.set(newArray, i, parsedItem);
            }
            return newArray;
        } else if (raw instanceof Collection) {
            Collection<?> collection = (Collection<?>) raw;
            int length = collection.size();
            Object newArray = Array.newInstance(componentType, length);
            int i = 0;
            for (Object item : collection) {
                Object parsedItem = parse(item, componentType, null, options, contextKeys);
                Array.set(newArray, i, parsedItem);
                i++;
            }
            return newArray;
        }
        throw new BeanException("Failed to parseArray: " + raw.getClass());
    }

}
